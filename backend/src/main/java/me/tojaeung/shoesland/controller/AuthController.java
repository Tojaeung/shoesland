package me.tojaeung.shoesland.controller;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import lombok.*;
import me.tojaeung.shoesland.dto.Response;
import me.tojaeung.shoesland.dto.request.MemberRequestDto;
import me.tojaeung.shoesland.dto.response.MemberResponseDto;
import me.tojaeung.shoesland.entity.Member;
import me.tojaeung.shoesland.enums.ExceptionCode;
import me.tojaeung.shoesland.exception.CustomException;
import me.tojaeung.shoesland.jwt.TokenProvider;
import me.tojaeung.shoesland.lib.Helper;
import me.tojaeung.shoesland.repository.MemberRepository;
import me.tojaeung.shoesland.service.MemberService;
import me.tojaeung.shoesland.util.CookieUtil;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {
  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final TokenProvider jwtTokenProvider;
  private final HttpServletRequest httpServletRequest;
  private final Response response;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Validated MemberRequestDto.Login login, Errors errors) {
    // validation check
    if (errors.hasErrors()) {
      return response.invalidFields(Helper.refineErrors(errors));
    }
    return memberService.login(login);
  }

  @GetMapping("/refresh")
  public ResponseEntity<?> refresh() {

    Cookie[] cookies = httpServletRequest.getCookies();

    String refreshToken = null;
    for (Cookie cookie : cookies) {
      if (cookie.getName() == "refreshToken") {
        refreshToken = cookie.getValue();
        break;
      }
    }

    // 리프레쉬 토큰 만료기한이 하루이하일때 재발급해준다.
    if (jwtTokenProvider.getExpiration(refreshToken) < 1000 * 60 * 60 * 24) {

      Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

      // 만료기한이 얼마 안남은 리프레쉬 토큰을 새로운 리프레쉬 토큰으로 덮어씌운다.
      CookieUtil.refreshToken(newRefreshToken);
    }

    // 리프레쉬 토큰이 존재할때 리턴
    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
      Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

      String memberEmail = authentication.getName();

      Member member = memberRepository.findByEmail(memberEmail).orElse(null);
      if (member == null) {
        throw new CustomException(ExceptionCode.MISMATCH_REFRESH_TOKEN);
      }

      String accessToken = jwtTokenProvider.generateAccessToken(authentication);

      MemberResponseDto.Refresh refresh = MemberResponseDto.Refresh
          .builder()
          .accessToken(accessToken)
          .member(member)
          .build();

      return ResponseEntity.ok().body(refresh);

    } else {
      // 리프레쉬 토큰이 존재하지 않는다면 로그아웃api 실행
      return null;
    }
  }
}
