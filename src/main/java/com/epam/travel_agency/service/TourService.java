package com.epam.travel_agency.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epam.travel_agency.dto.TourDTO;
import com.epam.travel_agency.entity.City;
import com.epam.travel_agency.entity.Tour;
import com.epam.travel_agency.repositories.CityRepository;
import com.epam.travel_agency.repositories.TourRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    public List<TourDTO> getAllTours() {
        log.debug("Fetching all tours");
        List<TourDTO> tours = tourRepository.findAll()
                .stream()
                .map(tour -> modelMapper.map(tour, TourDTO.class))
                .collect(Collectors.toList());
        log.info("Fetched {} tours", tours.size());
        return tours;
    }

    public TourDTO getTourById(Long id) {
        log.debug("Fetching tour by ID={}", id);
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tour not found with ID={}", id);
                    return new RuntimeException("Tour not found");
                });
        log.info("Tour found: ID={} Name={}", tour.getId(), tour.getName());
        return modelMapper.map(tour, TourDTO.class);
    }

    public TourDTO createTour(TourDTO tourDTO) {
        log.info("Creating new tour: {}", tourDTO.getName());
        log.debug("TourDTO received: {}", tourDTO);

        City city = cityRepository.findById(tourDTO.getCityId())
                .orElseThrow(() -> {
                    log.error("City not found with ID={}", tourDTO.getCityId());
                    return new RuntimeException("City not found");
                });

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
        log.info("Tour created with ID={} Name={}", saved.getId(), saved.getName());
        return modelMapper.map(saved, TourDTO.class);
    }

    public TourDTO updateTour(Long id, TourDTO tourDTO) {
        log.info("Updating tour ID={}", id);
        log.debug("TourDTO received for update: {}", tourDTO);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tour not found with ID={}", id);
                    return new RuntimeException("Tour not found");
                });

        City city = cityRepository.findById(tourDTO.getCityId())
                .orElseThrow(() -> {
                    log.error("City not found with ID={}", tourDTO.getCityId());
                    return new RuntimeException("City not found");
                });

        tour.setName(tourDTO.getName());
        tour.setCity(city);
        tour.setPrice(tourDTO.getPrice());
        tour.setStartDate(tourDTO.getStartDate());
        tour.setEndDate(tourDTO.getEndDate());
        tour.setAvailableSeats(tourDTO.getAvailableSeats());
        tour.setDescription(tourDTO.getDescription());

        Tour updated = tourRepository.save(tour);
        log.info("Tour updated: ID={} Name={}", updated.getId(), updated.getName());
        return modelMapper.map(updated, TourDTO.class);
    }

    public void deleteTour(Long id) {
        log.warn("Deleting tour ID={}", id);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tour not found with ID={}", id);
                    return new RuntimeException("Tour not found");
                });

        tourRepository.delete(tour);
        log.info("Tour deleted: ID={} Name={}", tour.getId(), tour.getName());
    }
}