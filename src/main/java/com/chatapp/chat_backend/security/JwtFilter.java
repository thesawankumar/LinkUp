package com.chatapp.chat_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Step 1: Header se token nikalo
        // "Authorization: Bearer eyJhbGci..."
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Token nahi hai — aage jaane do
            // SecurityConfig decide karega allow kare ya nahi
            filterChain.doFilter(request, response);
            return;
        }
        // Step 3: "Bearer " hata ke sirf token lo
        // "Bearer eyJhbGci..." → "eyJhbGci..."
        String token = authHeader.substring(7);

        // Step 4: Token valid hai?
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Step 5: Token se email nikalo
        String email = jwtUtil.extractEmail(token);

        // Step 6: Already authenticated? Dobara mat karo
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 7: Email se user DB mein dhundo
            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(email);

            // Step 8: Authentication object banao
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                           // Credentials null (token based)
                            userDetails.getAuthorities()    // ROLE_USER etc.
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // Step 9: Spring Security ko batao — yeh user logged in hai
            // Ab koi bhi controller mein @AuthenticationPrincipal se
            // yeh user mil sakta hai
            SecurityContextHolder.getContext()
                    .setAuthentication(authToken);

            // Step 10: Aage controller tak jaane do
            filterChain.doFilter(request, response);
        }
    }
}
