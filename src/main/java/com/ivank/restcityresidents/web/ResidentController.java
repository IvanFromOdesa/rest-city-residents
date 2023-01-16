package com.ivank.restcityresidents.web;

import com.ivank.restcityresidents.dto.resident.ResidentDeleteInfo;
import com.ivank.restcityresidents.dto.resident.ResidentInfoDto;
import com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto;
import com.ivank.restcityresidents.dto.resident.SaveResidentDto;
import com.ivank.restcityresidents.service.ResidentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/residents")
@AllArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResidentInfoDto createResident(@Valid @RequestBody SaveResidentDto requestForSave) {
        return residentService.createResident(requestForSave);
    }

    @PutMapping(value = "/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResidentInfoDto updateResident(@PathVariable("id") Long id,
                                          @RequestBody SaveResidentDto residentDto) {
        return residentService.updateResident(id, residentDto);
    }

    @GetMapping(value = "/all", params = {"page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<ResidentsAllInfoDto> getResidents(int page_num) {
        return residentService.getAllResidents(page_num);
    }

    @GetMapping(value = "/getResidentById/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResidentsAllInfoDto getResidentById(@PathVariable("id") Long id) {
        return residentService.getResidentById(id);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResidentDeleteInfo deleteResident(@PathVariable Long id) {
        residentService.removeResident(id);
        return new ResidentDeleteInfo("Resident with id " + id + " deleted successfully");
    }

    @GetMapping(value = "/findByFirstAndLastName", params = {"first_name", "last_name", "page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<ResidentsAllInfoDto> findByFirstAndLastName(String first_name, String last_name, int page_num) {
        return residentService.findResidentByFirstAndLastNames(first_name, last_name, page_num);
    }
}
