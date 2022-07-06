package me.tojaeung.shoesland.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends GenericFilterBean {

    private static String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Request Header 에서 JWT 토큰 추출
        String accessToken = resolveAccessToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {

            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        // 엑세스토큰이 존재하지 않는 경우

        // 엑세스토큰 만료일자 얼마 안남은 경우 재발급

        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        System.out.println(bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
