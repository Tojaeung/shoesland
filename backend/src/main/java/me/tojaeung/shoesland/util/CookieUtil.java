package me.tojaeung.shoesland.util;

import org.springframework.http.ResponseCookie;

import me.tojaeung.shoesland.jwt.TokenProvider;

public class CookieUtil {

  public static ResponseCookie refreshToken(String refreshToken) {
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .maxAge(TokenProvider.REFRESH_TOKEN_VALIDITY)
        .path("/api/refresh")
        .secure(true)
        .sameSite("None")
        .httpOnly(true)
        .build();

    return cookie;
  }
}
