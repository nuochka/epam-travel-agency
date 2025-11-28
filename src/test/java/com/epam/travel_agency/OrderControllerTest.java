package com.epam.travel_agency;


import com.epam.travel_agency.controller.OrderController;
import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void createOrder_success() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setTourId(1L);

        OrderResponseDto response = new OrderResponseDto();
        response.setUsername("user");

        when(orderService.createOrder(any(), eq("user"))).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void getMyOrders_success() throws Exception {
        OrderResponseDto order = new OrderResponseDto();
        order.setUsername("user");

        when(orderService.getAllByUsername("user")).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/me")
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"));
    }
}
