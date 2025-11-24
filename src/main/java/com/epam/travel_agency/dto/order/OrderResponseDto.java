package com.epam.travel_agency.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Long id;
    private String username;
    private String tourName;
    private LocalDateTime bookingDate;
    private OrderStatus status;
}
