package com.ivank.restcityresidents.repository;

import com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto;
import com.ivank.restcityresidents.entity.Resident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PostgreSQL's dialect (though native queries are not used).
 */
@Repository
public interface ResidentDao extends JpaRepository<Resident, Long> {

    // JPQL queries
    @Query(value = "SELECT new com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto " +
            "(r.firstName, r.lastName, r.dateOfBirth, r.city.officialName) FROM Resident AS r")
    List<ResidentsAllInfoDto> getAllResidents(Pageable pageable);

    @Query(value = "SELECT new com.ivank.restcityresidents.dto.resident.ResidentsAllInfoDto " +
            "(r.firstName, r.lastName, r.dateOfBirth, r.city.officialName) FROM Resident AS r\n" +
            "\tWHERE r.firstName = ?1 AND r.lastName = ?2")
    List<ResidentsAllInfoDto> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
}
