package com.ivank.restcityresidents.service.impl;

import com.ivank.restcityresidents.dto.resident.ResidentInfoDto;
import com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto;
import com.ivank.restcityresidents.dto.resident.SaveResidentDto;
import com.ivank.restcityresidents.entity.City;
import com.ivank.restcityresidents.entity.Resident;
import com.ivank.restcityresidents.exception.NotFoundException;
import com.ivank.restcityresidents.repository.CityDao;
import com.ivank.restcityresidents.repository.ResidentDao;
import com.ivank.restcityresidents.service.ResidentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResidentServiceImpl implements ResidentService {

    // May be edited later
    private final int PAGE_ELEMENTS_NUMBER = 3;
    private final ResidentDao residentDao;
    private final CityDao cityDao;

    @Override
    public List<ResidentsAllInfoDto> findResidentByFirstAndLastNames(String firstName,
                                                          String lastName,
                                                          int pageNumber) {
        if(pageNumber < 0) throw new IllegalArgumentException("Page number cannot be less than 0!");
        return residentDao.findAllByFirstNameAndLastName(
                firstName,
                lastName,
                PageRequest.of(pageNumber, PAGE_ELEMENTS_NUMBER));
    }

    @Override
    public ResidentInfoDto createResident(SaveResidentDto residentDto) {
        validateResident(residentDto);
        City residenceCity = validateCity(residentDto);
        Resident resident = convertToEntity(residentDto, residenceCity, new Resident());
        return convertToDto(resident, "created");
    }

    @Override
    public List<ResidentsAllInfoDto> getAllResidents(int pageNumber) {
        if(pageNumber < 0) throw new IllegalArgumentException("Page number cannot be less than 0!");
        return residentDao.getAllResidents(PageRequest.of(pageNumber, PAGE_ELEMENTS_NUMBER));
    }

    @Override
    public ResidentsAllInfoDto getResidentById(Long id) {
        Resident resident = getOrThrow(id);
        return allInfoDto(resident);
    }

    @Override
    public ResidentInfoDto updateResident(Long id,  SaveResidentDto residentDto) {
        Resident resident = getOrThrow(id);
        validateResident(residentDto);
        City newResidenceCity = validateCity(residentDto);
        Resident forDto = convertToEntity(residentDto, newResidenceCity, resident);
        return convertToDto(forDto, "updated");
    }

    @Override
    public void removeResident(Long id) {
        Resident resident = getOrThrow(id);
        residentDao.delete(resident);
    }

    /**
     * Validates resident's birth date
     * @param residentDto dto to validate
     */
    private void validateResident(SaveResidentDto residentDto) {
        if(!residentDto.getDateOfBirth().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date is invalid!");
        }
    }

    /**
     * Validates input city in dto
     * @param residentDto dto to validate
     * @return name of the city
     */
    private City validateCity(SaveResidentDto residentDto) {
        Optional<City> result = cityDao.findCityByOfficialName(residentDto.getCityName());
        if(result.isEmpty()) {
            throw new IllegalArgumentException("Specified city not found!");
        }
        return result.get();
    }

    /**
     * Attempts to find resident by his id.
     * @param id input id
     * @return found resident or throws custom exception
     */
    private Resident getOrThrow(Long id) {
        return residentDao.findById(id)
                .orElseThrow(()-> new NotFoundException("Resident not found for id: " + id ));
    }

    /**
     * Converts dto to entity for save to db
     * @param residentDto dto to fetch data from
     * @param city city in dto
     * @param resident resident to be saved
     * @return saved resident
     */
    private Resident convertToEntity(SaveResidentDto residentDto, City city, Resident resident) {
        resident.setFirstName(residentDto.getFirstName());
        resident.setLastName(residentDto.getLastName());
        resident.setDateOfBirth(residentDto.getDateOfBirth());
        resident.setCity(city);
        return residentDao.save(resident);
    }

    /**
     * Converts entity to dto.
     * @param resident resident to be mapped to dto
     * @param state either "created" or "updated"
     * @return success message
     */
    private ResidentInfoDto convertToDto(Resident resident, String state) {
        return ResidentInfoDto.builder()
                .fullNameResult(resident.getFirstName() + " "
                        + resident.getLastName() + " "
                        + state + " successfully!")
                .id(resident.getId())
                .build();
    }

    /**
     * Converts resident to dto with all necessary fields to display.
     * @param resident resident to fetch data from
     * @return dto with necessary fields
     */
    private ResidentsAllInfoDto allInfoDto(Resident resident) {
        return new ResidentsAllInfoDto(resident.getFirstName(),
                                       resident.getLastName(),
                                       resident.getDateOfBirth(),
                                       resident.getCity().getOfficialName());
    }
}
