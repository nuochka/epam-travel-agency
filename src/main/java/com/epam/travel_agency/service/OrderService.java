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

    public OrderResponseDto createOrder(OrderRequestDto dto, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));

        Integer seats = tour.getAvailableSeats();
        if (seats == null || seats <= 0) {
            throw new IllegalStateException("No available seats");
        }

        if (orderRepository.existsByUserIdAndTourId(user.getId(), tour.getId())) {
            throw new IllegalStateException("You already booked this tour");
        }

        Order order = Order.builder()
                .user(user)
                .tour(tour)
                .bookingDate(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .build();

        tour.setAvailableSeats(seats - 1);
        tourRepository.save(tour);

        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto approve(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto cancel(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        Tour tour = order.getTour();
        tour.setAvailableSeats(tour.getAvailableSeats() + 1);
        tourRepository.save(tour);

        return toResponse(order);
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
                .stream()
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

    public void deleteOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Not your order");
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled orders can be deleted");
        }

        orderRepository.delete(order);
    }
}