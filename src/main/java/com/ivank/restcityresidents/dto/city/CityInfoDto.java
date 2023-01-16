package com.ivank.restcityresidents.dto.city;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class CityInfoDto {
    private String result;
    private Long id;
}
