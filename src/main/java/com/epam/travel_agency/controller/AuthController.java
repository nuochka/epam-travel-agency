package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody UserRequestDto dto) {
        return userService.register(dto);
    }
}
