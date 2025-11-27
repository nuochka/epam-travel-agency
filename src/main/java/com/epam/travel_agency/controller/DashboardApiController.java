package com.epam.travel_agency.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.security.JwtUtil;
import com.epam.travel_agency.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashboardApiController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @GetMapping("/api/dashboard-info")
    public Map<String, Object> getDashboardInfo(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsername(username);
        boolean isAdmin = userService.isAdmin(user);

        return Map.of(
                "username", username,
                "isAdmin", isAdmin
        );
    }
}
