package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.request.LoginRequestDTO;
import com.project.appointmentmanager.dto.request.RegisterRequestDTO;
import com.project.appointmentmanager.dto.response.LoginResponseDTO;
import com.project.appointmentmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthService authService;

    // POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}