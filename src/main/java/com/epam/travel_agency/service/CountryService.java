package com.epam.travel_agency.service;

import com.epam.travel_agency.dto.country.CountryRequestDto;
import com.epam.travel_agency.dto.country.CountryResponseDto;
import com.epam.travel_agency.entity.Country;
import com.epam.travel_agency.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService  {

    private final CountryRepository countryRepository;
    private final ModelMapper mapper;

    public CountryResponseDto create(CountryRequestDto dto) {
        log.info("Creating new country: {}", dto.getName());
        log.debug("Incoming DTO: {}", dto);

        Country country = mapper.map(dto, Country.class);
        Country saved = countryRepository.save(country);

        log.info("Country created with ID={}", saved.getId());
        return mapper.map(saved, CountryResponseDto.class);
    }

    public CountryResponseDto getById(Long id) {
        log.debug("Fetching country by ID={}", id);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Country not found: ID={}", id);
                    return new RuntimeException("Country not found");
                });

        log.info("Country found: {}", country.getName());
        return mapper.map(country, CountryResponseDto.class);
    }

    public List<CountryResponseDto> getAll() {
        log.debug("Fetching all countries");

        List<CountryResponseDto> list = countryRepository.findAll()
                .stream()
                .map(c -> mapper.map(c, CountryResponseDto.class))
                .toList();

        log.info("Retrieved {} countries", list.size());
        return list;
    }

    public CountryResponseDto update(Long id, CountryRequestDto dto) {
        log.info("Updating country ID={}", id);
        log.debug("Update DTO: {}", dto);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Country update failed. Not found: ID={}", id);
                    return new RuntimeException("Country not found");
                });

        country.setName(dto.getName());
        Country updated = countryRepository.save(country);

        log.info("Updated country ID={} -> '{}'", id, updated.getName());
        return mapper.map(updated, CountryResponseDto.class);
    }

    public void delete(Long id) {
        log.warn("Deleting country ID={}", id);

        if (!countryRepository.existsById(id)) {
            log.error("Failed to delete. Country not found: ID={}", id);
            throw new RuntimeException("Country not found");
        }

        countryRepository.deleteById(id);
        log.info("Country ID={} deleted", id);
    }
}