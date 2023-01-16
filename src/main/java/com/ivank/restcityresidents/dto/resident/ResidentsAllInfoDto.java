package com.ivank.restcityresidents.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class ResidentsAllInfoDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String cityName; // Returns only the name of the city
}
