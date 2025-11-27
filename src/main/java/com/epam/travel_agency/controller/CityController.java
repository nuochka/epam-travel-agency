package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.city.CityResponseDto;
import com.epam.travel_agency.service.CityService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityResponseDto> getAllCities() {
        return cityService.getAll();
    }
}
