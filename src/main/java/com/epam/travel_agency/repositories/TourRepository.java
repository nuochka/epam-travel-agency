package com.epam.travel_agency.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epam.travel_agency.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {}
