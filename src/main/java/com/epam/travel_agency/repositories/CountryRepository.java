package com.epam.travel_agency.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epam.travel_agency.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {}
