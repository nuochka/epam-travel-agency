package com.epam.travel_agency;

import com.epam.travel_agency.controller.AuthController;
import com.epam.travel_agency.dto.user.*;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.security.JwtUtil;
import com.epam.travel_agency.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_success() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("test");
        request.setEmail("test@mail.com");
        request.setPassword("123");

        UserResponseDto response = new UserResponseDto();
        response.setUsername("test");
        response.setRole("ROLE_USER");

        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

   @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("123");

        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userService.findByUsername("test")).thenReturn(user);
        when(jwtUtil.generateToken("test")).thenReturn("token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.token").value("token"));
    }
}
