package com.example.nextgentech.Service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    // Inject UserRepository hoặc Service khác

    public void saveGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Kiểm tra xem user đã tồn tại chưa, nếu chưa thì lưu vào DB
        // Ví dụ: userRepository.findByEmail(email).orElse(save(newUser));
    }
}
