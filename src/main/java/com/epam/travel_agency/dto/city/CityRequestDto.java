package com.epam.travel_agency.dto.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CityRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private Long countryId;
}

