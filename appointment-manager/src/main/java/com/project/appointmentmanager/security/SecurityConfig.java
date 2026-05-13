package com.project.appointmentmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for JWT-based REST APIs
                .csrf(csrf -> csrf.disable())

                // Don't create sessions — JWT is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define route permissions
                .authorizeHttpRequests(auth -> auth

                        // Public routes — no token needed
                        .requestMatchers("/auth/**").permitAll()

                        // Admin-only routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Provider-only routes
                        .requestMatchers("/provider/**").hasRole("PROVIDER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/slots/**").hasRole("PROVIDER")

                        // All other routes require any valid JWT
                        .anyRequest().authenticated()
                )

                // Register our JwtFilter BEFORE Spring's default auth filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt bean — used in AuthService to hash/check passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}