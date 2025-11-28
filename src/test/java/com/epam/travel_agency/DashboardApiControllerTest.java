package com.epam.travel_agency;

import com.epam.travel_agency.controller.DashboardApiController;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.security.JwtUtil;
import com.epam.travel_agency.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardApiControllerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @InjectMocks
    private DashboardApiController dashboardApiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardApiController).build();
    }

    @Test
    void getDashboardInfo_success() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setRoles(null);

        when(jwtUtil.extractUsername("token")).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(user);
        when(userService.isAdmin(user)).thenReturn(true);

        mockMvc.perform(get("/api/dashboard-info")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.isAdmin").value(true));
    }
}

