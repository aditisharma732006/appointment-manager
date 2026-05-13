package com.project.appointmentmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read Authorization header
        // Format: "Bearer eyJhbGciOi..."
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Step 2: Strip "Bearer " prefix → get raw token
            String token = authHeader.substring(7);

            // Step 3: Validate it
            if (jwtUtil.isTokenValid(token)) {

                // Step 4: Extract user info from token payload
                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);

                // Step 5: Tell Spring Security "this user is authenticated"
                // ROLE_ prefix is required by Spring Security
//                Why "ROLE_" prefix? In SecurityConfig you wrote- java.hasRole("ADMIN")
//                Spring internally converts that to "ROLE_ADMIN" when checking.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // If token invalid → we just don't set authentication
            // Spring Security will return 401 automatically
        }

        // Step 6: Always pass request to the next filter/controller
        filterChain.doFilter(request, response);
    }
}