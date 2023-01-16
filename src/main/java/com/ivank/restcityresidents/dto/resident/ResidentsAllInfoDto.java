package com.ivank.restcityresidents.dto.resident;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ResidentsAllInfoDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String cityName; // Returns only the name of the city
}
