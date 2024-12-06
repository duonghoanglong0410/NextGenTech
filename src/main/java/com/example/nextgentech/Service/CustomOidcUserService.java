package com.example.nextgentech.Service;

import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    // Sử dụng constructor injection để đảm bảo userRepository không bị null
    @Autowired
    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isNewUser(String email) {
        // Kiểm tra trạng thái của người dùng trong cơ sở dữ liệu
        return !userRepository.existsByEmail(email);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getAttribute("email");
        String picture = oidcUser.getAttribute("picture");
        String givenName = oidcUser.getAttribute("given_name");
        String familyName = oidcUser.getAttribute("family_name");
        // Kiểm tra người dùng đã tồn tại chưa, nếu chưa thì lưu vào DB
        findByEmail(email).ifPresentOrElse(
                existingUser -> {
                    // Người dùng đã tồn tại
                },
                () -> {
                    // Người dùng chưa tồn tại, lưu vào DB và gán vai trò
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(oidcUser.getFullName()); // Sử dụng fullName nếu có
                    newUser.setRole("ROLE_USER"); // Gán vai trò mặc định
                    newUser.setPicture(picture);
                    newUser.setGivenName(givenName);
                    newUser.setFamilyName(familyName);
                    userRepository.save(newUser);
                }
        );
        return oidcUser;
    }
}
