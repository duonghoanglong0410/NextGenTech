package com.example.nextgentech.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        if (oidcUser != null) {
            // Lấy tên người dùng từ thuộc tính `name`, `given_name`, hoặc `family_name`
            String fullName = oidcUser.getAttribute("name"); // Hoặc "given_name" để lấy tên
            model.addAttribute("name", fullName != null ? fullName : "User");
        } else {
            model.addAttribute("name", "Guest");
        }
        return "layouts/user_layout"; // Trả về tên của view (ví dụ, welcome.html)
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        return "user/login";
    }
    @GetMapping("/my-account")
    public String getMyAccount(Model model) {
        return "user/my-account";
    }
}
