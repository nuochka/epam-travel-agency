package com.epam.travel_agency;

import com.epam.travel_agency.dto.TourDTO;
import com.epam.travel_agency.entity.City;
import com.epam.travel_agency.entity.Tour;
import com.epam.travel_agency.repositories.CityRepository;
import com.epam.travel_agency.repositories.TourRepository;
import com.epam.travel_agency.service.TourService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourServiceTest {

    private TourRepository tourRepository;
    private CityRepository cityRepository;
    private ModelMapper mapper;
    private TourService tourService;

    @BeforeEach
    void setUp() {
        tourRepository = mock(TourRepository.class);
        cityRepository = mock(CityRepository.class);
        mapper = new ModelMapper();
        tourService = new TourService(tourRepository, cityRepository, mapper);
    }

    @Test
    void getAllTours_success() {
        Tour tour = new Tour();
        tour.setName("Tour1");
        when(tourRepository.findAll()).thenReturn(List.of(tour));

        List<TourDTO> tours = tourService.getAllTours();
        assertEquals(1, tours.size());
        assertEquals("Tour1", tours.get(0).getName());
    }

    @Test
    void getTourById_success() {
        Tour tour = new Tour();
        tour.setName("Tour1");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        TourDTO dto = tourService.getTourById(1L);
        assertEquals("Tour1", dto.getName());
    }

    @Test
    void getTourById_notFound() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tourService.getTourById(1L));
        assertEquals("Tour not found", ex.getMessage());
    }

    @Test
    void createTour_success() {
        City city = new City();
        city.setId(1L);
        TourDTO dto = new TourDTO();
        dto.setName("Tour1");
        dto.setCityId(1L);
        dto.setPrice(100.0);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setAvailableSeats(10);

        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        TourDTO result = tourService.createTour(dto);
        assertEquals("Tour1", result.getName());
    }

    @Test
    void createTour_cityNotFound() {
        TourDTO dto = new TourDTO();
        dto.setCityId(1L);
        when(cityRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tourService.createTour(dto));
        assertEquals("City not found", ex.getMessage());
    }

    @Test
    void updateTour_success() {
        City city = new City();
        city.setId(1L);
        Tour tour = new Tour();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        TourDTO dto = new TourDTO();
        dto.setName("Updated");
        dto.setCityId(1L);
        dto.setPrice(100.0);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setAvailableSeats(5);

        TourDTO result = tourService.updateTour(1L, dto);
        assertEquals("Updated", result.getName());
    }

    @Test
    void updateTour_tourNotFound() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());
        TourDTO dto = new TourDTO();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tourService.updateTour(1L, dto));
        assertEquals("Tour not found", ex.getMessage());
    }

    @Test
    void deleteTour_success() {
        Tour tour = new Tour();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        tourService.deleteTour(1L);
        verify(tourRepository).delete(tour);
    }

    @Test
    void deleteTour_notFound() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tourService.deleteTour(1L));
        assertEquals("Tour not found", ex.getMessage());
    }
}

