package com.mot.mot.config;

import com.mot.mot.model.Role;
import com.mot.mot.model.User;
import com.mot.mot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, org.springframework.security.oauth2.core.user.OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email"); // Lấy email từ OAuth2 provider
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Nếu chưa có người dùng trong DB, tạo mới
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRoles(Set.of(Role.USER)); // Gán quyền mặc định
            userRepository.save(newUser);
        }

        return oauth2User;
    }
}
