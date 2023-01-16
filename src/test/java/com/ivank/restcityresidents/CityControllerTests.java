package com.ivank.restcityresidents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivank.restcityresidents.dto.city.CityInfoDto;
import com.ivank.restcityresidents.entity.City;
import com.ivank.restcityresidents.repository.CityDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = RestCityResidentsApplication.class)
@AutoConfigureMockMvc
public class CityControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        cityDao.deleteAll();
    }

    @Test
    public void createCity_success() throws Exception {

        String body = """
          {
              "officialName": "Odesa",
              "population": 900000,
              "area": 170,
              "timeZone": "EST"
          }
          """;

        MvcResult mvcResult = mvc.perform(post("/api/cities/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CityInfoDto response = parseResponse(mvcResult, CityInfoDto.class);
        String result = String.valueOf(response.getResult());

        assertEquals(result, "City Odesa created successfully!");
        City city = cityDao.findById(response.getId()).orElse(null);
        assertThat(city).isNotNull();
        assertThat(city.getOfficialName()).isEqualTo("Odesa");
        assertThat(city.getArea()).isEqualTo(170);
        assertThat(city.getPopulation()).isEqualTo(900000);
        assertThat(city.getTimeZone().name()).isEqualTo("EST");
    }

    @Test
    public void createCity_timeZoneInvalid() throws Exception {
        String body = """
          {
              "officialName": "Odesa",
              "population": 900000,
              "area": 170,
              "timeZone": "NIDJ"
          }
          """;

        MvcResult mvcResult = mvc.perform(post("/api/cities/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isForbidden())
                .andReturn();

        assertEquals("{\"status\":403," +
                "\"error\":\"Forbidden\"," +
                "\"message\":\"Invalid timezone\"}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void getAllCities_success() throws Exception {
        createSampleCity();
        createSampleCity();
        MvcResult mvcResult = mvc.perform(get("/api/cities/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page_num", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
        // Assert that we get what we saved in db previously
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
        assertTrue(result.contains("\"officialName\":\"Blbla\""));
    }

    private void createSampleCity() throws Exception {

        MvcResult mvcResultCreate = mvc.perform(post("/api/cities/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidJSONCity())
                )
                .andExpect(status().isCreated()).andReturn();

        CityInfoDto responseC = parseResponse(mvcResultCreate, CityInfoDto.class);
        String result = String.valueOf(responseC.getResult());

        assertEquals(result, "City" + " Blbla" + " created successfully!");

        Long cityId = responseC.getId();
        City city = cityDao.findById(cityId).orElse(null);
        assertThat(city).isNotNull();
    }

    private String getValidJSONCity() {
        return """
          {"officialName":"Blbla","population":390233,"area":600,"timeZone":"EST"}""";
    }

    private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }
}
