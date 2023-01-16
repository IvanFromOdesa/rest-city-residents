package com.ivank.restcityresidents.service;

import com.ivank.restcityresidents.dto.city.CitiesAllInfoDto;
import com.ivank.restcityresidents.dto.city.CityInfoDto;
import com.ivank.restcityresidents.dto.city.SaveCityDto;

import java.util.List;

public interface CityService {

    /**
     * Creates city from dto.
     * @param cityDto dto to fetch data from
     * @return success message
     */
    CityInfoDto createCity(SaveCityDto cityDto);

    /**
     * Retrieve all cities with pagination.
     * @param pageNumber number of the retrieved cities page.
     * @return all cities present in db.
     */
    List<CitiesAllInfoDto> getAllCities(int pageNumber);
}
