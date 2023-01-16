package com.ivank.restcityresidents.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public class ResidentInfoDto {
    private String fullNameResult;
}
