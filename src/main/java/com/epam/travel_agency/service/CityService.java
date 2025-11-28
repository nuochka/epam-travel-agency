package com.epam.travel_agency.service;

import com.epam.travel_agency.dto.city.CityRequestDto;
import com.epam.travel_agency.dto.city.CityResponseDto;
import com.epam.travel_agency.entity.City;
import com.epam.travel_agency.entity.Country;
import com.epam.travel_agency.repositories.CityRepository;
import com.epam.travel_agency.repositories.CountryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityResponseDto create(CityRequestDto dto) {
        log.info("Creating new city: {}", dto.getName());
        log.debug("CityRequestDto received: {}", dto);

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> {
                    log.error("Country with id={} not found for city '{}'", dto.getCountryId(), dto.getName());
                    return new RuntimeException("Country not found");
                });

        City city = new City();
        city.setName(dto.getName());
        city.setCountry(country);

        City saved = cityRepository.save(city);

        log.info("City created with id={}", saved.getId());
        return toResponse(saved);
    }

    public CityResponseDto getById(Long id) {
        log.info("Fetching city with id={}", id);

        City city = cityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("City with id={} not found", id);
                    return new RuntimeException("City not found");
                });

        log.debug("Fetched city: {}", city);
        return toResponse(city);
    }

    public List<CityResponseDto> getAll() {
        log.info("Fetching all cities");

        List<City> cities = cityRepository.findAll();

        log.debug("Found {} cities", cities.size());
        return cities.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CityResponseDto> getByCountry(Long countryId) {
        log.info("Fetching cities for country id={}", countryId);

        List<City> cities = cityRepository.findByCountryId(countryId);

        log.debug("Country id={} has {} cities", countryId, cities.size());
        return cities.stream()
                .map(this::toResponse)
                .toList();
    }

    public CityResponseDto update(Long id, CityRequestDto dto) {
        log.info("Updating city with id={}", id);
        log.debug("Update request: {}", dto);

        City city = cityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("City with id={} not found for update", id);
                    return new RuntimeException("City not found");
                });

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> {
                    log.error("Country with id={} not found during update of city {}", dto.getCountryId(), id);
                    return new RuntimeException("Country not found");
                });

        city.setName(dto.getName());
        city.setCountry(country);

        City updated = cityRepository.save(city);

        log.info("City with id={} updated successfully", id);
        return toResponse(updated);
    }

    public void delete(Long id) {
        log.warn("Deleting city with id={}", id);

        if (!cityRepository.existsById(id)) {
            log.error("Attempted to delete non-existing city id={}", id);
            throw new RuntimeException("City not found");
        }

        cityRepository.deleteById(id);

        log.info("City {} deleted", id);
    }

    private CityResponseDto toResponse(City city) {
        CityResponseDto dto = new CityResponseDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setCountryId(city.getCountry().getId());
        dto.setCountryName(city.getCountry().getName());
        return dto;
    }
}
