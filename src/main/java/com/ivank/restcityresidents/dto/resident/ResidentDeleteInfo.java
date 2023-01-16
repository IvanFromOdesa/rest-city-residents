package com.ivank.restcityresidents.dto.resident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@AllArgsConstructor
@Getter
public class ResidentDeleteInfo {
    private String result;
}
