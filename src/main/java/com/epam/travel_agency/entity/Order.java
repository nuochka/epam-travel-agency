package com.epam.travel_agency.entity;

import java.time.LocalDateTime;

import com.epam.travel_agency.dto.order.OrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    private LocalDateTime bookingDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}

