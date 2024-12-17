package com.example.nextgentech.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Lấy thông tin từ Google User Info
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        System.out.println("User logged in: " + email + " - " + name);

        // Redirect to homepage hoặc xử lý logic đăng nhập tùy chỉnh
        response.sendRedirect("/");
    }
}
