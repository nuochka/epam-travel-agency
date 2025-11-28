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
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public OrderResponseDto createOrder(OrderRequestDto dto, String username) {

        log.info("Creating order for user '{}' and tour ID={}", username, dto.getTourId());
        log.debug("OrderRequestDto received: {}", dto);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> {
                    log.error("Tour not found: ID={}", dto.getTourId());
                    return new IllegalArgumentException("Tour not found");
                });

        Integer seats = tour.getAvailableSeats();
        if (seats == null || seats <= 0) {
            log.warn("Attempt to book with no available seats for tour ID={}", dto.getTourId());
            throw new IllegalStateException("No available seats");
        }

        if (orderRepository.existsByUserIdAndTourId(user.getId(), tour.getId())) {
            log.warn("Duplicate booking attempt detected for user '{}' and tour ID={}", username, dto.getTourId());
            throw new IllegalStateException("You already booked this tour");
        }

        Order order = Order.builder()
                .user(user)
                .tour(tour)
                .bookingDate(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .build();

        log.info("Order created (NEW). Decreasing seats for tour ID={} from {} to {}", 
                 tour.getId(), seats, seats - 1);

        tour.setAvailableSeats(seats - 1);
        tourRepository.save(tour);

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with ID={}", savedOrder.getId());

        return toResponse(savedOrder);
    }

    public OrderResponseDto approve(Long orderId) {
        log.info("Approving order ID={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: ID={}", orderId);
                    return new IllegalArgumentException("Order not found");
                });

        order.setStatus(OrderStatus.PAID);
        log.info("Order ID={} approved (PAID)", orderId);

        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto cancel(Long orderId) {
        log.warn("Cancelling order ID={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found while cancelling: ID={}", orderId);
                    return new IllegalArgumentException("Order not found");
                });

        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.warn("Order ID={} already cancelled. Duplicate cancel attempt.", orderId);
            throw new IllegalStateException("Order already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        Tour tour = order.getTour();
        tour.setAvailableSeats(tour.getAvailableSeats() + 1);
        tourRepository.save(tour);

        log.info("Order ID={} cancelled. Seats restored for tour ID={}", orderId, tour.getId());

        return toResponse(order);
    }

    public List<OrderResponseDto> getAll() {
        log.debug("Fetching all orders");

        List<OrderResponseDto> list = orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("Retrieved {} orders", list.size());
        return list;
    }

    public List<OrderResponseDto> getAllByUsername(String username) {
        log.debug("Fetching orders for username '{}'", username);

        List<OrderResponseDto> list = orderRepository.findAllByUserUsername(username)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("User '{}' has {} orders", username, list.size());
        return list;
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
        log.warn("User '{}' attempting to delete order ID={}", username, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found during deletion: ID={}", orderId);
                    return new IllegalArgumentException("Order not found");
                });

        if (!order.getUser().getUsername().equals(username)) {
            log.error("Unauthorized delete attempt: user '{}' tried to delete order ID={} owned by '{}'", 
                      username, orderId, order.getUser().getUsername());
            throw new IllegalStateException("Not your order");
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            log.warn("Delete attempt for non-cancelled order ID={}. Status={}", orderId, order.getStatus());
            throw new IllegalStateException("Only cancelled orders can be deleted");
        }

        orderRepository.delete(order);
        log.info("Order ID={} successfully deleted by user '{}'", orderId, username);
    }
}