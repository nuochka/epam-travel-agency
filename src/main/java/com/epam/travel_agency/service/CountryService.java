package com.epam.travel_agency.service;

import com.epam.travel_agency.dto.country.CountryRequestDto;
import com.epam.travel_agency.dto.country.CountryResponseDto;
import com.epam.travel_agency.entity.Country;
import com.epam.travel_agency.repositories.CountryRepository;
import com.epam.travel_agency.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService  {

    private final CountryRepository countryRepository;
    private final ModelMapper mapper;

    public CountryResponseDto create(CountryRequestDto dto) {
        Country country = mapper.map(dto, Country.class);
        return mapper.map(countryRepository.save(country), CountryResponseDto.class);
    }

    public CountryResponseDto getById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));
        return mapper.map(country, CountryResponseDto.class);
    }

    public List<CountryResponseDto> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(c -> mapper.map(c, CountryResponseDto.class))
                .toList();
    }

    public CountryResponseDto update(Long id, CountryRequestDto dto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        country.setName(dto.getName());
        return mapper.map(countryRepository.save(country), CountryResponseDto.class);
    }

    public void delete(Long id) {
        countryRepository.deleteById(id);
    }
}
