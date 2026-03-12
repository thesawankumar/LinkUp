package com.chatapp.chat_backend.config;

import com.chatapp.chat_backend.security.JwtFilter;
import com.chatapp.chat_backend.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter  jwtFilter;
    private final OAuth2SuccessHandler  oAuth2SuccessHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        return http
                // CSRF disable — JWT use kar rahe hain, session nahi
                .csrf(AbstractHttpConfigurer::disable)

                // Session mat banao — har request self-contained hai
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL rules
                .authorizeHttpRequests(auth -> auth

                        // Yeh sab OPEN hain — token nahi chahiye
                        .requestMatchers(
                                "/api/auth/**",    // register, login, otp
                                "/oauth2/**",      // google login
                                "/ws/**"           // websocket
                        ).permitAll()

                        // Baaki sab ke liye token COMPULSORY hai
                        .anyRequest().authenticated()
                )

                // Google OAuth2 setup
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )

                // JwtFilter ko UsernamePasswordAuthenticationFilter
                // se PEHLE lagao
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    // Password hashing ke liye — BCrypt sabse secure hai
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager — login verify karne ke liye
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
