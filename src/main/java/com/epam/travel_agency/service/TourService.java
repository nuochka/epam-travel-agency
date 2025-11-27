package com.epam.travel_agency.service;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epam.travel_agency.dto.TourDTO;
import com.epam.travel_agency.entity.City;
import com.epam.travel_agency.entity.Tour;
import com.epam.travel_agency.repositories.CityRepository;
import com.epam.travel_agency.repositories.TourRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    public List<TourDTO> getAllTours() {
        return tourRepository.findAll()
                .stream()
                .map(tour -> modelMapper.map(tour, TourDTO.class))
                .collect(Collectors.toList());
    }

    public TourDTO getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        return modelMapper.map(tour, TourDTO.class);
    }

    public TourDTO createTour(TourDTO tourDTO) {
        City city = cityRepository.findById(tourDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        Tour tour = Tour.builder()
                .name(tourDTO.getName())
                .city(city)
                .price(tourDTO.getPrice())
                .startDate(tourDTO.getStartDate())
                .endDate(tourDTO.getEndDate())
                .availableSeats(tourDTO.getAvailableSeats())
                .description(tourDTO.getDescription())
                .build();
        Tour saved = tourRepository.save(tour);
        return modelMapper.map(saved, TourDTO.class);
        }

        public TourDTO updateTour(Long id, TourDTO tourDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        City city = cityRepository.findById(tourDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        tour.setName(tourDTO.getName());
        tour.setCity(city);
        tour.setPrice(tourDTO.getPrice());
        tour.setStartDate(tourDTO.getStartDate());
        tour.setEndDate(tourDTO.getEndDate());
        tour.setAvailableSeats(tourDTO.getAvailableSeats());
        tour.setDescription(tourDTO.getDescription());

        Tour updated = tourRepository.save(tour);
        return modelMapper.map(updated, TourDTO.class);
        }

    public void deleteTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        tourRepository.delete(tour);
    }
}

