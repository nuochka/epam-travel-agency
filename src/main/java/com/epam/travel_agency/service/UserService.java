package com.epam.travel_agency.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.entity.UserRole;
import com.epam.travel_agency.repositories.UserRepository;
import com.epam.travel_agency.repositories.UserRolesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponseDto register(UserRequestDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        UserRole role = new UserRole(savedUser.getId(), "ROLE_USER");
        userRolesRepository.save(role);

        UserResponseDto response = mapper.map(savedUser, UserResponseDto.class);
        response.setRole("ROLE_USER");

        return response;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
