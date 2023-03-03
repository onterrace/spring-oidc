package com.jirepos.oauth2.handlers;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공시 처리하는 핸들러이다.
 */
// @Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler  {

    public AuthSuccessHandler(String targetUrl) {
        super();
        setDefaultTargetUrl(targetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        // 보통 UserDetails를 상속받아 권한을 관리하는데 이때 컨트롤러 단에서 Authentication 객체를 받으면 Principal을 받아 쓸 수 있다.
        // what the getPrincipal() returns depends on the authentication provider
        // Object o = authentication.getPrincipal();
        
        super.onAuthenticationSuccess(request, response, chain, authentication);
    }
}
