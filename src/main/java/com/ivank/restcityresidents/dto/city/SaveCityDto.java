package com.ivank.restcityresidents.dto.city;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Jacksonized
public class SaveCityDto {

    @NotBlank(message = "official city name must be included!")
    private String officialName;

    @NotNull(message = "population must be included!")
    @Digits(integer = 8, fraction = 0) // 99 999 999 is limit
    private Long population;

    @NotNull(message = "area must be included!")
    @Digits(integer = 5, fraction = 2) // 99 999.99 (in km2) is limit
    private Double area;

    @NotBlank(message = "city timezone must be included!")
    private String timeZone;
}
