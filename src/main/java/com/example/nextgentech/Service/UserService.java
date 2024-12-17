package com.example.nextgentech.Service;

import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    // Inject UserRepository hoặc Service khác

    @Autowired
    private UserRepository userRepository;

    public void saveGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Kiểm tra xem user đã tồn tại chưa, nếu chưa thì lưu vào DB
        // Ví dụ: userRepository.findByEmail(email).orElse(save(newUser));
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
