package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.request.LoginRequestDTO;
import com.project.appointmentmanager.dto.request.RegisterRequestDTO;
import com.project.appointmentmanager.dto.response.LoginResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.exception.UserNotFoundException;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.UserRepository;
import com.project.appointmentmanager.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private SlotService slotService;

    public String register(RegisterRequestDTO dto) {

        // build user from dto
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        // hash password before saving — never save raw password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        // if registering as provider → create provider profile + generate slots
        if (dto.getRole() == Role.PROVIDER) {
            ServiceProvider provider = new ServiceProvider();
            provider.setName(dto.getName());
            provider.setCategory(dto.getCategory());
            provider.setDescription(dto.getDescription());
            provider.setLocation(dto.getLocation());
            provider.setUser(user);

            providerRepository.save(provider);

            // generate slots for next 5 days immediately
            slotService.generateSlotsForProvider(provider);
        }

        return "Registered successfully";
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        // find user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + dto.getEmail()));

        // check password against hashed password in DB
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // generate token with email + role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponseDTO(token, user.getRole().name());
    }
}