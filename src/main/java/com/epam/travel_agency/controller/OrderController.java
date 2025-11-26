package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto create(
            @RequestBody OrderRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return orderService.createOrder(dto, userDetails.getUsername());
    }

    @PutMapping("/{id}/approve")
    public OrderResponseDto approve(@PathVariable Long id) {
        return orderService.approve(id);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponseDto cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }

    @GetMapping
    public List<OrderResponseDto> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/me")
    @ResponseBody
    public List<OrderResponseDto> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getAllByUsername(userDetails.getUsername());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId, Principal principal) {
        orderService.deleteOrder(orderId, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Order deleted"));
    }
}
