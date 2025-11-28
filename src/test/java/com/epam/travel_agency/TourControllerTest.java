package com.epam.travel_agency;

import com.epam.travel_agency.controller.TourController;
import com.epam.travel_agency.dto.TourDTO;
import com.epam.travel_agency.service.TourService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TourControllerTest {

    @Mock
    private TourService tourService;

    @InjectMocks
    private TourController tourController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tourController).build();
    }

    @Test
    void getAllTours_success() throws Exception {
        TourDTO tour = new TourDTO();
        tour.setName("Tour1");
        when(tourService.getAllTours()).thenReturn(List.of(tour));

        mockMvc.perform(get("/tours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tour1"));
    }

    @Test
    void getTourById_success() throws Exception {
        TourDTO tour = new TourDTO();
        tour.setName("Tour1");
        when(tourService.getTourById(1L)).thenReturn(tour);

        mockMvc.perform(get("/tours/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tour1"));
    }
}
