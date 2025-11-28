package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.user.LoginRequest;
import com.epam.travel_agency.dto.user.LoginResponse;
import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.security.JwtUtil;
import com.epam.travel_agency.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public String getLogin() {
        log.debug("GET /auth/login page requested");
        return "auth/login";
    }

    @GetMapping("/register")
    public String getRegister() {
        log.debug("GET /auth/register page requested");
        return "auth/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public UserResponseDto register(@Valid @RequestBody UserRequestDto dto) {
        log.info("Registering new user: {}", dto.getUsername());

        UserResponseDto response = userService.register(dto);

        log.debug("User successfully registered: {}", response.getUsername());
        return response;
    }

    @PostMapping("/login")
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest dto, HttpServletRequest request) {
        log.info("Login attempt for username: {}", dto.getUsername());

        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
        } catch (Exception e) {
            log.warn("Failed login attempt for username: {}", dto.getUsername());
            throw e;
        }

        var user = userService.findByUsername(dto.getUsername());
        String token = jwtUtil.generateToken(dto.getUsername());

        request.getSession(true).setAttribute("username", user.getUsername());

        log.info("User logged in successfully: {}", user.getUsername());
        log.debug("JWT generated for user: {}", user.getUsername());

        return new LoginResponse(token, user.getId(), user.getUsername());
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");

            log.info("User logging out: {}", username);
            session.invalidate();
        } else {
            log.debug("Logout attempt without an active session");
        }

        return "redirect:/auth/login";
    }
}