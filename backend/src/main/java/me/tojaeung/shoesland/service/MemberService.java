package me.tojaeung.shoesland.service;

import me.tojaeung.shoesland.entity.Member;
import me.tojaeung.shoesland.enums.Authority;
import me.tojaeung.shoesland.enums.ErrorCode;
import me.tojaeung.shoesland.exception.CustomException;
import me.tojaeung.shoesland.jwt.JwtFilter;
import me.tojaeung.shoesland.jwt.TokenProvider;
import me.tojaeung.shoesland.dto.Response;
import me.tojaeung.shoesland.dto.request.MemberRequestDto;
import me.tojaeung.shoesland.dto.response.MemberResponseDto;
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
            return response.fail("이미 회원가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        Member meember = Member.builder()
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        MemberRepository.save(meember);

        return response.success("회원가입에 성공했습니다.");
    }

    public ResponseEntity<?> login(MemberRequestDto.Login login) {

        Member member = MemberRepository.findByEmail(login.getEmail()).orElse(null);

        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                login.getEmail(), login.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername
        // 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
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
    // // 1. Refresh Token 검증
    // if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
    // return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
    // }

    // // 2. Access Token 에서 User email 을 가져옵니다.
    // Authentication authentication =
    // jwtTokenProvider.getAuthentication(reissue.getAccessToken());

    // // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
    // String refreshToken = (String) redisTemplate.opsForValue().get("RT:" +
    // authentication.getName());
    // // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
    // if (ObjectUtils.isEmpty(refreshToken)) {
    // return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
    // }
    // if (!refreshToken.equals(reissue.getRefreshToken())) {
    // return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
    // }

    // // 4. 새로운 토큰 생성
    // MemberResponseDto.TokenInfo tokenInfo =
    // jwtTokenProvider.generateToken(authentication);

    // // 5. RefreshToken Redis 업데이트
    // redisTemplate.opsForValue()
    // .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
    // tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

    // return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    // }

    // public ResponseEntity<?> logout() {

    // 1. Access Token 검증
    // if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
    // return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
    // }

    // 2. Access Token 에서 User email 을 가져옵니다.
    // Authentication authentication =
    // jwtTokenProvider.getAuthentication(logout.getAccessToken());

    // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
    // if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) !=
    // null) {
    // // Refresh Token 삭제
    // redisTemplate.delete("RT:" + authentication.getName());
    // }

    // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
    // Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
    // redisTemplate.opsForValue()
    // .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

    // return response.success("로그아웃 되었습니다.");
    // }

    public ResponseEntity<?> authority() {
        // SecurityContext에 담겨 있는 authentication userEamil 정보
        String userEmail = SecurityUtil.getCurrentUserEmail();

        Member member = MemberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

        // add ROLE_ADMIN
        member.getRoles().add(Authority.ROLE_ADMIN.name());
        MemberRepository.save(member);

        return response.success();
    }

    public ResponseEntity<?> refresh() {

        // SecurityContext에 담겨 있는 authentication userEamil 정보
        String userEmail = SecurityUtil.getCurrentUserEmail();

        Member member = MemberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

        // add ROLE_ADMIN
        member.getRoles().add(Authority.ROLE_ADMIN.name());
        MemberRepository.save(member);

        return response.success();
    }

}
