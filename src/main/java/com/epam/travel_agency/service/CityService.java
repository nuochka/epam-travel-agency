package com.epam.travel_agency.service;

import com.epam.travel_agency.dto.city.CityRequestDto;
import com.epam.travel_agency.dto.city.CityResponseDto;
import com.epam.travel_agency.entity.City;
import com.epam.travel_agency.entity.Country;
import com.epam.travel_agency.repositories.CityRepository;
import com.epam.travel_agency.repositories.CountryRepository;
import com.epam.travel_agency.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityResponseDto create(CityRequestDto dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        City city = new City();
        city.setName(dto.getName());
        city.setCountry(country);

        return toResponse(cityRepository.save(city));
    }

    public CityResponseDto getById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
        return toResponse(city);
    }

    public List<CityResponseDto> getAll() {
        return cityRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CityResponseDto> getByCountry(Long countryId) {
        return cityRepository.findByCountryId(countryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CityResponseDto update(Long id, CityRequestDto dto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        city.setName(dto.getName());
        city.setCountry(country);

        return toResponse(cityRepository.save(city));
    }

    public void delete(Long id) {
        cityRepository.deleteById(id);
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
