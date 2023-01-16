package com.ivank.restcityresidents.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
public class SaveResidentDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String cityName; // Client only specifies the name of the city
}
