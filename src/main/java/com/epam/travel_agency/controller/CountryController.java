package com.epam.travel_agency.controller;

import com.epam.travel_agency.dto.country.CountryRequestDto;
import com.epam.travel_agency.dto.country.CountryResponseDto;
import com.epam.travel_agency.service.CountryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Slf4j
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    public CountryResponseDto create(@Valid @RequestBody CountryRequestDto dto) {
        log.info("Creating new country: {}", dto.getName());
        log.debug("CountryRequestDto received: {}", dto);

        CountryResponseDto created = countryService.create(dto);

        log.info("Country created with id={}", created.getId());
        return created;
    }

    @GetMapping("/{id}")
    public CountryResponseDto getById(@PathVariable Long id) {
        log.info("Fetching country with id={}", id);

        CountryResponseDto country = countryService.getById(id);

        log.debug("Fetched country: {}", country);
        return country;
    }

    @GetMapping
    public List<CountryResponseDto> getAll() {
        log.info("Fetching all countries");

        List<CountryResponseDto> countries = countryService.getAll();

        log.debug("Found {} countries", countries.size());
        return countries;
    }

    @PutMapping("/{id}")
    public CountryResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody CountryRequestDto dto
    ) {
        log.info("Updating country with id={}", id);
        log.debug("Update request: {}", dto);

        CountryResponseDto updated = countryService.update(id, dto);

        log.info("Country {} updated successfully", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.warn("Deleting country with id={}", id);

        countryService.delete(id);

        log.info("Country {} deleted", id);
    }
}