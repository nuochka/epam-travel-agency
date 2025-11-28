package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            Principal principal
    ) {
        return orderService.createOrder(dto, principal.getName());
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
    public List<OrderResponseDto> getMyOrders(Principal principal) {
        return orderService.getAllByUsername(principal.getName());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId, Principal principal) {
        orderService.deleteOrder(orderId, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Order deleted"));
    }
}
