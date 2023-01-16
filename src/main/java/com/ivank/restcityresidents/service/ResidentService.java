package com.ivank.restcityresidents.service;

import com.ivank.restcityresidents.dto.resident.ResidentInfoDto;
import com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto;
import com.ivank.restcityresidents.dto.resident.SaveResidentDto;
import com.ivank.restcityresidents.entity.Resident;

import java.util.List;

public interface ResidentService {

    /**
     * Returns list of residents with the specified same both first and last names using pagination.
     * The list size is determined by elements number of the page specified in the implementation.
     * Page number represents the chunk of the dataset with an elements number of the page.
     * @param firstName first name of resident
     * @param lastName last name of resident
     * @param pageNumber number of the dataset "chunk"
     * @return list of found residents in the chunk with specified pageNumber
     */
    List<ResidentsAllInfoDto> findResidentByFirstAndLastNames(String firstName, String lastName, int pageNumber);

    /**
     * Creates resident from dto.
     * @param resident dto to fetch data from
     * @return success message
     */
    ResidentInfoDto createResident(SaveResidentDto resident);

    /**
     * Retrieves all residents.
     * @param pageNumber number of retrieved residents page
     *                  (can be tons - we do not want to send everything at once)
     * @return all residents present in db.
     */
    List<ResidentsAllInfoDto> getAllResidents(int pageNumber);

    /**
     * Gets resident by his id.
     * @param id resident's id
     * @return necessary info about resident in form of dto
     */
    ResidentsAllInfoDto getResidentById(Long id);

    /**
     * Updates resident by his id.
     * @param id resident's id
     * @param residentDto dto to fetch data from
     * @return success message
     */
    ResidentInfoDto updateResident(Long id, SaveResidentDto residentDto);

    /**
     * Removes resident from the db
     * @param id resident's id
     */
    void removeResident(Long id);
}
