package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto create(
            @RequestBody OrderRequestDto dto,
            Principal principal
    ) {
        log.info("User '{}' is creating an order", principal.getName());
        log.debug("OrderRequestDto received: {}", dto);

        OrderResponseDto response = orderService.createOrder(dto, principal.getName());

        log.info("Order created with id={}", response.getId());
        return response;
    }

    @PutMapping("/{id}/approve")
    public OrderResponseDto approve(@PathVariable Long id) {
        log.info("Approving order with id={}", id);

        OrderResponseDto response = orderService.approve(id);

        log.debug("Order {} approved", id);
        return response;
    }

    @PutMapping("/{id}/cancel")
    public OrderResponseDto cancel(@PathVariable Long id) {
        log.info("Canceling order with id={}", id);

        OrderResponseDto response = orderService.cancel(id);

        log.debug("Order {} canceled", id);
        return response;
    }

    @GetMapping
    public List<OrderResponseDto> getAll() {
        log.debug("Fetching all orders");
        List<OrderResponseDto> orders = orderService.getAll();

        log.info("Returned {} orders", orders.size());
        return orders;
    }

    @GetMapping("/me")
    public List<OrderResponseDto> getMyOrders(Principal principal) {
        log.info("Fetching orders for user '{}'", principal.getName());

        List<OrderResponseDto> orders = orderService.getAllByUsername(principal.getName());

        log.debug("User '{}' has {} orders", principal.getName(), orders.size());
        return orders;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(
            @PathVariable Long orderId,
            Principal principal
    ) {
        log.warn("User '{}' is deleting order {}", principal.getName(), orderId);

        orderService.deleteOrder(orderId, principal.getName());

        log.info("Order {} deleted successfully", orderId);

        return ResponseEntity.ok(Map.of("message", "Order deleted"));
    }
}
