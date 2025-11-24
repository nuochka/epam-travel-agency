package com.epam.travel_agency.dto.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CountryRequestDto {
    @NotBlank(message = "Country name cannot be empty")
    private String name;
}
