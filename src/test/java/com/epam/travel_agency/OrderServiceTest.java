package com.epam.travel_agency;

import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.dto.order.OrderStatus;
import com.epam.travel_agency.entity.Order;
import com.epam.travel_agency.entity.Tour;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.repositories.OrderRepository;
import com.epam.travel_agency.repositories.TourRepository;
import com.epam.travel_agency.repositories.UserRepository;
import com.epam.travel_agency.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private TourRepository tourRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        tourRepository = mock(TourRepository.class);
        orderService = new OrderService(orderRepository, userRepository, tourRepository);
    }

    @Test
    void createOrder_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setAvailableSeats(5);
        OrderRequestDto dto = new OrderRequestDto();
        dto.setTourId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(orderRepository.existsByUserIdAndTourId(1L, 1L)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponseDto response = orderService.createOrder(dto, "user");
        assertEquals("user", response.getUsername());
        assertEquals(OrderStatus.NEW, response.getStatus());
        assertEquals(4, tour.getAvailableSeats());
    }

    @Test
    void createOrder_noSeats_throws() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setAvailableSeats(0);
        OrderRequestDto dto = new OrderRequestDto();
        dto.setTourId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(dto, "user"));
        assertEquals("No available seats", ex.getMessage());
    }

    @Test
    void approveOrder_success() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponseDto response = orderService.approve(1L);
        assertEquals(OrderStatus.PAID, response.getStatus());
    }

    @Test
    void cancelOrder_success() {
        Tour tour = new Tour();
        tour.setAvailableSeats(2);
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);
        order.setTour(tour);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponseDto response = orderService.cancel(1L);
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertEquals(3, tour.getAvailableSeats());
    }

    @Test
    void deleteOrder_success() {
        User user = new User();
        user.setUsername("user");
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        orderService.deleteOrder(1L, "user");
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_notCancelled_throws() {
        User user = new User();
        user.setUsername("user");
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderService.deleteOrder(1L, "user"));
        assertEquals("Only cancelled orders can be deleted", ex.getMessage());
    }

    @Test
    void deleteOrder_notOwner_throws() {
        User user = new User();
        user.setUsername("user");
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUser(new User());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderService.deleteOrder(1L, "user"));
        assertEquals("Not your order", ex.getMessage());
    }
}

