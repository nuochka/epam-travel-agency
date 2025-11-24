package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.order.OrderRequestDto;
import com.epam.travel_agency.dto.order.OrderResponseDto;
import com.epam.travel_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto create(@RequestBody OrderRequestDto dto) {
        return orderService.createOrder(dto);
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
}
