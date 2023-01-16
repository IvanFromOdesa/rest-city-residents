package com.ivank.restcityresidents.service.impl;

import com.ivank.restcityresidents.dto.city.CitiesAllInfoDto;
import com.ivank.restcityresidents.dto.city.CityInfoDto;
import com.ivank.restcityresidents.dto.city.SaveCityDto;
import com.ivank.restcityresidents.entity.City;
import com.ivank.restcityresidents.entity.CityTimeZone;
import com.ivank.restcityresidents.repository.CityDao;
import com.ivank.restcityresidents.service.CityService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CityServiceImpl implements CityService {

    // May be edited later
    private final int PAGE_ELEMENTS_NUMBER = 3;
    private final CityDao cityDao;

    @Override
    public CityInfoDto createCity(SaveCityDto cityDto) {
        validateCity(cityDto);
        City city = new City();
        City forDto = convertToEntity(cityDto, city);
        return convertToDto(forDto);
    }

    @Override
    public List<CitiesAllInfoDto> getAllCities(int pageNumber) {
       if(pageNumber < 0) throw new IllegalArgumentException("Page number cannot be less than 0!");
       List<City> cities = cityDao.getAllCities(PageRequest.of(pageNumber, PAGE_ELEMENTS_NUMBER));
       return convertListToDto(cities);
    }

    /**
     * Validates dto for population, area and timeZone.
     * @param cityDto dto to validate
     */
    private void validateCity(SaveCityDto cityDto) {
        if(cityDto.getArea() < 0 || cityDto.getPopulation() < 0) {
            throw new IllegalArgumentException("Either area or population less than 0");
        }
        for(CityTimeZone timeZone : CityTimeZone.values()) {
            if(timeZone.name().equals(cityDto.getTimeZone())) {
                return;
            }
        }
        throw new IllegalArgumentException("Invalid timezone");
    }

    /**
     * Converts dto to entity for save in db.
     * @param cityDto dto to fetch data from
     * @param city city to be saved in db
     * @return saved city
     */
    private City convertToEntity(SaveCityDto cityDto, City city) {
        city.setOfficialName(cityDto.getOfficialName());
        city.setArea(cityDto.getArea());
        city.setPopulation(cityDto.getPopulation());
        city.setTimeZone(CityTimeZone.valueOf(cityDto.getTimeZone()));
        return cityDao.save(city);
    }

    /**
     * Converts city to dto (for success message)
     * @param city city to fetch data from
     * @return success message
     */
    private CityInfoDto convertToDto(City city) {
        return CityInfoDto.builder()
                .result("City " + city.getOfficialName() + " created successfully!")
                .id(city.getId())
                .build();
    }

    /**
     * Converts list of cities to list of dto with necessary fields
     * @param cities list of cities
     * @return list of dto with necessary fields
     */
    private List<CitiesAllInfoDto> convertListToDto(List<City> cities) {
        return cities.stream().map(this::toInfoDto).collect(Collectors.toList());
    }

    /**
     * Converts city to dto with all necessary fields
     * @param city city to fetch data from
     * @return dto with necessary fields
     */
    private CitiesAllInfoDto toInfoDto(City city) {
        return CitiesAllInfoDto.builder()
                .officialName(city.getOfficialName())
                .population(city.getPopulation())
                .area(city.getArea())
                .timeZone(city.getTimeZone().name())
                .build();
    }
}
