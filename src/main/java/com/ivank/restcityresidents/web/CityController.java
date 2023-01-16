package com.ivank.restcityresidents.web;

import com.ivank.restcityresidents.dto.city.CitiesAllInfoDto;
import com.ivank.restcityresidents.dto.city.CityInfoDto;
import com.ivank.restcityresidents.dto.city.SaveCityDto;
import com.ivank.restcityresidents.service.CityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cities")
@AllArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CityInfoDto createCity(@Valid @RequestBody SaveCityDto requestForSave) {
        return cityService.createCity(requestForSave);
    }

    @GetMapping(value = "/all", params = {"page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<CitiesAllInfoDto> getCities(int page_num) {
        return cityService.getAllCities(page_num);
    }
}
