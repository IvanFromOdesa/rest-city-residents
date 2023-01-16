package com.ivank.restcityresidents.dto.resident;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class ResidentInfoDto {
    private String fullNameResult;
    private Long id;
}
