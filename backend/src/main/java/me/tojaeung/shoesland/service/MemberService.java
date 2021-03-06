package me.tojaeung.shoesland.service;

import me.tojaeung.shoesland.enums.Authority;
import me.tojaeung.shoesland.enums.ExceptionCode;
import me.tojaeung.shoesland.exception.CustomException;
import me.tojaeung.shoesland.jwt.JwtFilter;
import me.tojaeung.shoesland.jwt.TokenProvider;
import me.tojaeung.shoesland.dto.Response;
import me.tojaeung.shoesland.dto.request.MemberRequestDto;
import me.tojaeung.shoesland.dto.response.MemberResponseDto;
import me.tojaeung.shoesland.entity.Member;
import me.tojaeung.shoesland.repository.MemberRepository;
import me.tojaeung.shoesland.util.CookieUtil;
import me.tojaeung.shoesland.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository MemberRepository;
    private final Response response;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider jwtTokenProvider;
    private final JwtFilter jwtAuthenticationFilter;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public ResponseEntity<?> signUp(MemberRequestDto.SignUp signUp) {
        if (MemberRepository.existsByEmail(signUp.getEmail())) {
            return response.fail("?????? ??????????????? ??????????????????.", HttpStatus.BAD_REQUEST);
        }

        Member meember = Member.builder()
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        MemberRepository.save(meember);

        return response.success("??????????????? ??????????????????.");
    }

    public ResponseEntity<?> login(MemberRequestDto.Login login) {

        Member member = MemberRepository.findByEmail(login.getEmail()).orElse(null);

        if (member == null) {
            throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        // 1. Login ID/PW ??? ???????????? Authentication ?????? ??????
        // ?????? authentication ??? ?????? ????????? ???????????? authenticated ?????? false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                login.getEmail(), login.getPassword());

        // 2. ?????? ?????? (????????? ???????????? ??????)??? ??????????????? ??????
        // authenticate ???????????? ????????? ??? CustomUserDetailsService ?????? ?????? loadUserByUsername
        // ???????????? ??????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CookieUtil.refreshToken(refreshToken);

        MemberResponseDto.Login loginDto = MemberResponseDto.Login.builder()
                .accessToken(accessToken)
                .member(member)
                .build();

        return new ResponseEntity<>(loginDto, HttpStatus.OK);
    }

    // public ResponseEntity<?> reissue(MemberRequestDto.Reissue reissue) {
    // // 1. Refresh Token ??????
    // if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
    // return response.fail("Refresh Token ????????? ???????????? ????????????.", HttpStatus.BAD_REQUEST);
    // }

    // // 2. Access Token ?????? User email ??? ???????????????.
    // Authentication authentication =
    // jwtTokenProvider.getAuthentication(reissue.getAccessToken());

    // // 3. Redis ?????? User email ??? ???????????? ????????? Refresh Token ?????? ???????????????.
    // String refreshToken = (String) redisTemplate.opsForValue().get("RT:" +
    // authentication.getName());
    // // (??????) ?????????????????? Redis ??? RefreshToken ??? ???????????? ?????? ?????? ??????
    // if (ObjectUtils.isEmpty(refreshToken)) {
    // return response.fail("????????? ???????????????.", HttpStatus.BAD_REQUEST);
    // }
    // if (!refreshToken.equals(reissue.getRefreshToken())) {
    // return response.fail("Refresh Token ????????? ???????????? ????????????.", HttpStatus.BAD_REQUEST);
    // }

    // // 4. ????????? ?????? ??????
    // MemberResponseDto.TokenInfo tokenInfo =
    // jwtTokenProvider.generateToken(authentication);

    // // 5. RefreshToken Redis ????????????
    // redisTemplate.opsForValue()
    // .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
    // tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

    // return response.success(tokenInfo, "Token ????????? ?????????????????????.", HttpStatus.OK);
    // }

    // public ResponseEntity<?> logout() {

    // 1. Access Token ??????
    // if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
    // return response.fail("????????? ???????????????.", HttpStatus.BAD_REQUEST);
    // }

    // 2. Access Token ?????? User email ??? ???????????????.
    // Authentication authentication =
    // jwtTokenProvider.getAuthentication(logout.getAccessToken());

    // 3. Redis ?????? ?????? User email ??? ????????? Refresh Token ??? ????????? ????????? ?????? ??? ?????? ?????? ???????????????.
    // if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) !=
    // null) {
    // // Refresh Token ??????
    // redisTemplate.delete("RT:" + authentication.getName());
    // }

    // 4. ?????? Access Token ???????????? ????????? ?????? BlackList ??? ????????????
    // Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
    // redisTemplate.opsForValue()
    // .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

    // return response.success("???????????? ???????????????.");
    // }

    public ResponseEntity<?> authority() {
        // SecurityContext??? ?????? ?????? authentication userEamil ??????
        String userEmail = SecurityUtil.getCurrentUserEmail();

        Member member = MemberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

        // add ROLE_ADMIN
        member.getRoles().add(Authority.ROLE_ADMIN.name());
        MemberRepository.save(member);

        return response.success();
    }

    public ResponseEntity<?> refresh() {

        // SecurityContext??? ?????? ?????? authentication userEamil ??????
        String userEmail = SecurityUtil.getCurrentUserEmail();

        Member member = MemberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

        // add ROLE_ADMIN
        member.getRoles().add(Authority.ROLE_ADMIN.name());
        MemberRepository.save(member);

        return response.success();
    }

}
