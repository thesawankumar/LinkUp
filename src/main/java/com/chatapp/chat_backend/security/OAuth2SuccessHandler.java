package com.chatapp.chat_backend.security;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.enums.AuthProvider;
import com.chatapp.chat_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // Google se user info lo
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email   = oAuth2User.getAttribute("email");
        String name    = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // User hai toh lo, nahi hai toh banao
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePicture(picture);
                    newUser.setProvider(AuthProvider.GOOGLE);
                    return userRepository.save(newUser);
                });

        // Profile picture update karo (Google pe change ho sakti hai)
        user.setProfilePicture(picture);
        userRepository.save(user);

        // JWT banao
        String token = jwtUtil.generateToken(email);

        // React frontend par redirect karo token ke saath
        // Frontend yahan se token extract karega
        String redirectUrl = frontendUrl
                + "/oauth2/redirect?token=" + token;

        getRedirectStrategy()
                .sendRedirect(request, response, redirectUrl);
    }
}
