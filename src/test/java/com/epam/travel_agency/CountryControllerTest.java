package com.epam.travel_agency;

import com.epam.travel_agency.controller.CountryController;
import com.epam.travel_agency.dto.country.CountryRequestDto;
import com.epam.travel_agency.dto.country.CountryResponseDto;
import com.epam.travel_agency.service.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    void createCountry_success() throws Exception {
        CountryRequestDto request = new CountryRequestDto();
        request.setName("France");

        CountryResponseDto response = new CountryResponseDto();
        response.setId(1L);
        response.setName("France");

        when(countryService.create(any())).thenReturn(response);

        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("France"));
    }

    @Test
    void getAll_success() throws Exception {
        CountryResponseDto country = new CountryResponseDto();
        country.setName("France");

        when(countryService.getAll()).thenReturn(List.of(country));

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("France"));
    }
}

