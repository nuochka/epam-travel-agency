package com.epam.travel_agency.controller;

import lombok.RequiredArgsConstructor;
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
public class TourController {

    private final TourService tourService;

    @GetMapping
    public List<TourDTO> getAllTours() {
        return tourService.getAllTours();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<TourDTO> createTour(@Valid @RequestBody TourDTO tourDTO) {
        TourDTO createdTour = tourService.createTour(tourDTO);
        return ResponseEntity.ok(createdTour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable Long id, @RequestBody TourDTO tourDTO) {
        return ResponseEntity.ok(tourService.updateTour(id, tourDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view")
    public String getAllTours(Model model) {
        List<TourDTO> tours = tourService.getAllTours();
        model.addAttribute("tours", tours);
        return "tours/list";
    }

    @GetMapping("/view/{id}")
    public String getTourById(@PathVariable Long id, Model model) {
        TourDTO tour = tourService.getTourById(id);
        model.addAttribute("tour", tour);
        return "tours/details";
    }
}
