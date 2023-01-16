package com.ivank.restcityresidents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@AllArgsConstructor
public class ResidentDeleteInfo {
    private String result;
}
