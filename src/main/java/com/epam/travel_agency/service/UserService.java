package com.epam.travel_agency.service;

import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.entity.UserRole;
import com.epam.travel_agency.repositories.UserRepository;
import com.epam.travel_agency.repositories.UserRolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponseDto register(UserRequestDto dto) {
        log.info("Registering new user: {}", dto.getUsername());
        log.debug("User registration DTO: {}", dto);

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.error("Username already exists: {}", dto.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.error("Email already exists: {}", dto.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User successfully registered: ID={} Username={}", savedUser.getId(), savedUser.getUsername());

        UserRole role = new UserRole(savedUser.getId(), "ROLE_USER");
        userRolesRepository.save(role);
        log.debug("Assigned default role ROLE_USER to user ID={}", savedUser.getId());

        UserResponseDto response = mapper.map(savedUser, UserResponseDto.class);
        response.setRole("ROLE_USER");

        return response;
    }

    public User findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });
        log.info("User found: ID={} Username={}", user.getId(), user.getUsername());
        return user;
    }

    public boolean isAdmin(User user) {
        if (user == null) {
            log.debug("User is null, cannot be admin");
            return false;
        }
        boolean admin = user.getRoles().stream().anyMatch(r -> r.getRole().equals("ROLE_ADMIN"));
        log.debug("User ID={} Username={} isAdmin={}", user.getId(), user.getUsername(), admin);
        return admin;
    }
}
