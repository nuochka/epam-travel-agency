package com.epam.travel_agency.dto.city;

import lombok.Data;

@Data
public class CityResponseDto {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
}