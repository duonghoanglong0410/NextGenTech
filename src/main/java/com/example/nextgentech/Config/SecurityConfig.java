package com.example.nextgentech.Config;

import com.example.nextgentech.Service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

@Configuration
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt CSRF nếu không cần bảo vệ CSRF
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Cho phép tài nguyên tĩnh (CSS, JS, images) mà không cần đăng nhập
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                        .anyRequest().authenticated() // Các request khác đều phải đăng nhập
                        )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            // Custom success handler logic for redirection
                            Object principal = authentication.getPrincipal();
                            if (principal instanceof DefaultOidcUser) {
                                DefaultOidcUser user = (DefaultOidcUser) principal;

                                // Kiểm tra nếu đây là người dùng mới dựa trên email hoặc thông tin khác
                                String email = user.getEmail(); // Giả định bạn có phương thức lấy email
                                boolean isNewUser = customOidcUserService.isNewUser(email);

                                if (isNewUser) {
                                    response.sendRedirect("/welcome");
                                } else {
                                    response.sendRedirect("/");
                                }
                            } else {
                                response.sendRedirect("/home"); // Default redirection
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );
        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            response.sendRedirect("https://accounts.google.com/Logout");
        };
    }
}