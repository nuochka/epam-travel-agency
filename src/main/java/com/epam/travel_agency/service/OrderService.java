package com.epam.travel_agency.service;


import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.dto.order.OrderStatus;
import com.epam.travel_agency.entity.Order;
import com.epam.travel_agency.entity.Tour;
import com.epam.travel_agency.entity.User;
import com.epam.travel_agency.repositories.OrderRepository;
import com.epam.travel_agency.repositories.TourRepository;
import com.epam.travel_agency.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public OrderResponseDto createOrder(OrderRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        Order order = Order.builder()
                .user(user)
                .tour(tour)
                .bookingDate(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .build();

        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto approve(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponseDto> getAllByUsername(String username) {
    return orderRepository.findAllByUserUsername(username)
            .stream()
            .map(this::toResponse)
            .toList();
}


    private OrderResponseDto toResponse(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setTourName(order.getTour().getName());
        dto.setBookingDate(order.getBookingDate());
        dto.setStatus(order.getStatus());
        return dto;
    }
}