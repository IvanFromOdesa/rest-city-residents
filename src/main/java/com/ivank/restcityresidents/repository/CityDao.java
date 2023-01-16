package com.ivank.restcityresidents.repository;

import com.ivank.restcityresidents.dto.city.CitiesAllInfoDto;
import com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto;
import com.ivank.restcityresidents.entity.City;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityDao extends JpaRepository<City, Long> {

    // For validation in ResidentService
    // PostgreSQL native query
    // Best practice to specify the query manually.
    @Query(value = "SELECT * FROM public.city\n" +
            "\tWHERE official_name = ?1", nativeQuery = true)
    Optional<City> findCityByOfficialName(String name);

    // Can't use JPQL since we need to pass timeZone as String in dto, but we get enum objects
    // Handle in Service
    @Query(value = "SELECT * FROM public.city", nativeQuery = true)
    List<City> getAllCities(Pageable pageable);
}
