package com.ivank.restcityresidents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivank.restcityresidents.dto.resident.ResidentInfoDto;
import com.ivank.restcityresidents.entity.Resident;
import com.ivank.restcityresidents.repository.CityDao;
import com.ivank.restcityresidents.repository.ResidentDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = RestCityResidentsApplication.class)
@AutoConfigureMockMvc
public class ResidentControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private ObjectMapper objectMapper;

    // Add city before each test
    @BeforeEach
    public void beforeEach() throws Exception {

        String body = """
          {
              "officialName": "Odesa",
              "population": 900000,
              "area": 170,
              "timeZone": "EST"
          }
          """;

        mvc.perform(post("/api/cities/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated());
    }

    @AfterEach
    public void afterEach() {
        residentDao.deleteAll();
        cityDao.deleteAll();
    }

    @Test
    public void createResident_success() throws Exception {

        String firstName = "Desmond";
        String lastName = "Jackson";
        String cityName = "Odesa";

        MvcResult mvcResult = mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidJSONResident(firstName, lastName, cityName))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ResidentInfoDto response = parseResponse(mvcResult, ResidentInfoDto.class);
        String result = String.valueOf(response.getFullNameResult());

        assertEquals(result, firstName + " " + lastName + " created successfully!");

        Resident resident = residentDao.findById(response.getId()).orElse(null);
        assertThat(resident).isNotNull();
        assertThat(resident.getFirstName()).isEqualTo(firstName);
        assertThat(resident.getLastName()).isEqualTo(lastName);
        assertThat(resident.getCity().getOfficialName()).isEqualTo(cityName);
    }

    @Test
    public void createResident_validationFailure() throws Exception {
        mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createResident_validationLocalDateParseFailure() throws Exception {

        String body = """
          {
              "firstName": "Desmond",
              "lastName": "Jackson",
              "dateOfBirth": "2000-50-12",
              "cityName": "Odesa"
          }
          """;

        MvcResult mvcResult = mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isBadRequest()).andReturn();

        assertEquals("{\"status\":400," +
                        "\"error\":\"Bad Request\"," +
                        "\"message\":\"LocalDate field deserialization failed! Either empty or invalid pattern!\"}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void createResident_validationLocalDateInvalid() throws Exception {
        String body = """
          {
              "firstName": "Desmond",
              "lastName": "Jackson",
              "dateOfBirth": "2100-12-12",
              "cityName": "Odesa"
          }
          """;

        MvcResult mvcResult = mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isForbidden()).andReturn();

        assertEquals("{\"status\":403," +
                        "\"error\":\"Forbidden\"," +
                        "\"message\":\"Birth date is invalid!\"}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void createResident_validationCityInvalid() throws Exception {
        String body = """
          {
              "firstName": "Desmond",
              "lastName": "Jackson",
              "dateOfBirth": "2002-12-12",
              "cityName": "Kyiv"
          }
          """; // Kyiv not present in db

        MvcResult mvcResult = mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isForbidden()).andReturn();

        assertEquals("{\"status\":403," +
                        "\"error\":\"Forbidden\"," +
                        "\"message\":\"Specified city not found!\"}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void updateResident_success() throws Exception {

        long residentId = createSampleResident("Ivan", "K.", "Odesa");

        MvcResult mvcResultUpdate = mvc.perform(put("/api/residents/update/" + residentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidJSONResident("Ivan", "Krylosov", "Odesa"))
                )
                .andExpect(status().isOk()).andReturn();

        // Check if resident was updated
        ResidentInfoDto responseU = parseResponse(mvcResultUpdate, ResidentInfoDto.class);
        String res = String.valueOf(responseU.getFullNameResult());

        assertEquals(res, "Ivan" + " " + "Krylosov" + " updated successfully!");
    }

    @Test
    public void updateResident_idNotFound() throws Exception {

        createSampleResident("Ivan", "K.", "Odesa");

        MvcResult mvcResult = mvc.perform(put("/api/residents/update/" + 999) // Random Id
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidJSONResident("Ivan", "Krylosov", "Odesa"))
                )
                .andExpect(status().isBadRequest()).andReturn();

        assertEquals("{\"status\":400," +
                        "\"error\":\"Bad Request\"," +
                        "\"message\":\"Resident not found for id: 999\"}",
                mvcResult.getResponse().getContentAsString());
    }

    // Only success, as "id not found" is similar to updateResident_idNotFound()
    @Test
    public void getResidentById_success() throws Exception {

        String firstName = "Ivan";
        String lastName = "K.";
        String cityName = "Odesa";

        long idResident = createSampleResident(firstName, lastName, cityName);

        MvcResult mvcResult = mvc.perform(get("/api/residents/getResidentById/" + idResident)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk()).andReturn();

        // Assert that the resident passed for save is same as retrieved one
        assertEquals(getValidJSONResident(firstName, lastName, cityName),
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void removeResidentById_success() throws Exception {
        long idResident = createSampleResident("Ivan", "K.", "Odesa");

        MvcResult mvcResult= mvc.perform(delete("/api/residents/delete/" + idResident)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        // Assert that the resident deleted successfully
        assertEquals("{\"result\":\"Resident with id " + idResident + " deleted successfully\"}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void getAllResidents_success() throws Exception {
        createSampleResident("Ivd", "Bb", "Odesa");
        createSampleResident("Smh", "NJ", "Odesa");
        MvcResult mvcResult = mvc.perform(get("/api/residents/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page_num", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
        // Assert that we get what we saved in db previously
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
        assertTrue(result.contains("\"firstName\":\"Ivd\""));
        assertTrue(result.contains("\"firstName\":\"Smh\""));
    }

    @Test
    public void getAllResidentsByFirstAndLastNames_success() throws Exception {
        createSampleResident("Ivd", "Bb", "Odesa");
        createSampleResident("Ivd", "NoNo", "Odesa");
        createSampleResident("Ivd", "Bb", "Odesa");
        createSampleResident("Ivd", "Bb", "Odesa");
        createSampleResident("Ivd", "Bb", "Odesa");
        MvcResult mvcResult = mvc.perform(get("/api/residents/findByFirstAndLastName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("first_name", "Ivd")
                        .param("last_name", "Bb")
                        .param("page_num", "0")) // Only 3 results at first page of results
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andReturn();
        // Assert that we get these (3 results with same first and last names)
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
        assertTrue(result.contains("\"firstName\":\"Ivd\""));
    }

    // Same goes for getAll() - pageNum can't be less than 0
    @Test
    public void getAllResidentsByFirstAndLastNames_paramFailure() throws Exception {
        createSampleResident("Ivd", "Bb", "Odesa");
        createSampleResident("Ivd", "NoNo", "Odesa");
        MvcResult mvcResult = mvc.perform(get("/api/residents/findByFirstAndLastName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("first_name", "Ivd")
                        .param("last_name", "Bb")
                        .param("page_num", "-23"))
                .andExpect(status().isForbidden())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"message\":\"Page number cannot be less than 0!\""));
    }

    private long createSampleResident(String firstName,
                                      String lastName,
                                      String cityName) throws Exception {
        // First we create resident
        MvcResult mvcResultCreate = mvc.perform(post("/api/residents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidJSONResident(firstName, lastName, cityName))
                )
                .andExpect(status().isCreated()).andReturn();

        // Check if the resident was created
        ResidentInfoDto responseC = parseResponse(mvcResultCreate, ResidentInfoDto.class);
        String result = String.valueOf(responseC.getFullNameResult());

        assertEquals(result, firstName + " " + lastName + " created successfully!");

        // Get the saved resident
        Long residentId = responseC.getId();
        Resident resident = residentDao.findById(residentId).orElse(null);
        assertThat(resident).isNotNull();

        return residentId;
    }

    private String getValidJSONResident(String firstName, String lastName, String cityName) {
        return """
          {"firstName":"%s","lastName":"%s","dateOfBirth":"2001-12-12","cityName":"%s"}"""
                .formatted(firstName, lastName, cityName);
    }

    /**
     * Parses mvcResult to JSON format
     * @param mvcResult result from mock
     * @param c class to be used for JSON display
     * @return class that represents the JSON display
     * @param <T> class to be mapped
     */
    private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }
}
