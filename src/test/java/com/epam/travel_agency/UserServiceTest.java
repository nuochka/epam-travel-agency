package com.epam.travel_agency;

import com.epam.travel_agency.dto.user.UserRequestDto;
import com.epam.travel_agency.dto.user.UserResponseDto;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.entity.UserRole;
import com.epam.travel_agency.repositories.UserRepository;
import com.epam.travel_agency.repositories.UserRolesRepository;
import com.epam.travel_agency.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserRolesRepository userRolesRepository;
    private ModelMapper mapper;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userRolesRepository = mock(UserRolesRepository.class);
        mapper = new ModelMapper();
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, userRolesRepository, mapper, passwordEncoder);
    }

    @Test
    void register_success() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("test");
        dto.setEmail("test@mail.com");
        dto.setPassword("123");

        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("test");
        savedUser.setEmail("test@mail.com");
        savedUser.setPassword("encoded");
        when(userRepository.save(any())).thenReturn(savedUser);

        UserResponseDto response = userService.register(dto);

        assertEquals("test", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        verify(userRolesRepository).save(any(UserRole.class));
    }

    @Test
    void register_usernameExists_throws() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("test");
        when(userRepository.existsByUsername("test")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(dto));
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void register_emailExists_throws() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("test");
        dto.setEmail("test@mail.com");
        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(dto));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void findByUsername_success() {
        User user = new User();
        user.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("test");
        assertEquals(user, result);
    }

    @Test
    void findByUsername_notFound() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.findByUsername("test"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void isAdmin_returnsTrue() {
        User user = new User();
        UserRole role = new UserRole();
        role.setRole("ROLE_ADMIN");
        user.setRoles(Collections.singleton(role));

        assertTrue(userService.isAdmin(user));
    }

    @Test
    void isAdmin_returnsFalse() {
        assertFalse(userService.isAdmin(null));

        User user = new User();
        user.setRoles(new HashSet<>());
        assertFalse(userService.isAdmin(user));
    }
}
