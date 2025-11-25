package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.user.LoginRequest;
import com.epam.travel_agency.dto.user.LoginResponse;
import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.security.JwtUtil;
import com.epam.travel_agency.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public String getLogin() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String getRegister() {
        return "auth/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public UserResponseDto register(@Valid @RequestBody UserRequestDto dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest dto) {
        System.out.println("Login attempt for: " + dto.getUsername());
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        String token = jwtUtil.generateToken(dto.getUsername());
        return new LoginResponse(token);
    }
}
