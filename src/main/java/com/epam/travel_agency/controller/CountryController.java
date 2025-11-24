package com.epam.travel_agency.controller;


import com.epam.travel_agency.dto.country.CountryRequestDto;
import com.epam.travel_agency.dto.country.CountryResponseDto;
import com.epam.travel_agency.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    public CountryResponseDto create(@Valid @RequestBody CountryRequestDto dto) {
        return countryService.create(dto);
    }

    @GetMapping("/{id}")
    public CountryResponseDto getById(@PathVariable Long id) {
        return countryService.getById(id);
    }

    @GetMapping
    public List<CountryResponseDto> getAll() {
        return countryService.getAll();
    }

    @PutMapping("/{id}")
    public CountryResponseDto update(@PathVariable Long id,
                                     @Valid @RequestBody CountryRequestDto dto) {
        return countryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        countryService.delete(id);
    }
}