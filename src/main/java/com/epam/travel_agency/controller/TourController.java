package com.epam.travel_agency.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epam.travel_agency.dto.TourDTO;
import com.epam.travel_agency.service.TourService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Slf4j
public class TourController {

    private final TourService tourService;

    @GetMapping
    public List<TourDTO> getAllTours() {
        log.debug("Request received: GET /tours");
        List<TourDTO> tours = tourService.getAllTours();
        log.info("Returned {} tours", tours.size());
        return tours;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        log.debug("Request received: GET /tours/{}", id);
        TourDTO tour = tourService.getTourById(id);
        log.info("Tour found with id={}", id);
        return ResponseEntity.ok(tour);
    }

    @PostMapping("/create")
    public ResponseEntity<TourDTO> createTour(@Valid @RequestBody TourDTO tourDTO) {
        log.info("Creating a new tour: {}", tourDTO.getName());
        TourDTO createdTour = tourService.createTour(tourDTO);
        log.debug("Tour created with id={}", createdTour.getId());
        return ResponseEntity.ok(createdTour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable Long id, @RequestBody TourDTO tourDTO) {
        log.info("Updating tour with id={}", id);
        TourDTO updated = tourService.updateTour(id, tourDTO);
        log.debug("Tour updated: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        log.warn("Deleting tour with id={}", id);
        tourService.deleteTour(id);
        log.info("Tour successfully deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view")
    public String viewAllTours(Model model) {
        log.debug("Rendering tours list page");
        List<TourDTO> tours = tourService.getAllTours();
        model.addAttribute("tours", tours);
        log.info("Tours list page loaded, {} tours displayed", tours.size());
        return "tours/list";
    }

    @GetMapping("/view/{id}")
    public String viewTourById(@PathVariable Long id, Model model) {
        log.debug("Rendering details page for tour id={}", id);
        TourDTO tour = tourService.getTourById(id);
        model.addAttribute("tour", tour);
        log.info("Tour details page loaded for id={}", id);
        return "tours/details";
    }
}