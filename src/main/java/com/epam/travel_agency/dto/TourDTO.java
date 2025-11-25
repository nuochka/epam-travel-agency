package com.epam.travel_agency.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDTO {
    private Long id;
    private String name;
    private Long cityId;
    private String cityName;
    private Double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer availableSeats;
}
