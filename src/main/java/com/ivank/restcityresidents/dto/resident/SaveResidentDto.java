package com.ivank.restcityresidents.dto.resident;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ivank.restcityresidents.util.LocalDateDeserializer;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
public class SaveResidentDto {

    @NotBlank(message = "first name can't be empty!")
    private String firstName;

    @NotBlank(message = "last name can't be empty!")
    private String lastName;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "city of residence must be included!")
    private String cityName; // Client only specifies the name of the city
}
