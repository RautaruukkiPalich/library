package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.config.TestSecurityConfig;
import com.app.utils.JsonTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class AuthorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String PREFIX_JSON_PATH = "/controllers/author/json";
    private final static String BASE_API_AUTHOR_PATH = "/api/authors/";

    @BeforeEach
    void setUp() {
        cleanupDatabase();
    }

    @Transactional
    public void cleanupDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE;");
    }

    @Test
    void createAuthor_shouldReturn201andAuthorId() throws Exception {
        final String JSON_PATH = "/requests/create-valid-author.json";
        String requestBody = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + JSON_PATH);

        MvcResult res = mockMvc.perform(
                        post(BASE_API_AUTHOR_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = res.getResponse().getHeader("Location");
        assertNotNull(location);
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(
                        get(BASE_API_AUTHOR_PATH + id)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("firstname").isNotEmpty())
                .andExpect(jsonPath("lastname").isNotEmpty())
                .andExpect(jsonPath("surname").isNotEmpty())
                .andExpect(jsonPath("created_at").isNotEmpty())
                .andExpect(jsonPath("updated_at").isNotEmpty());
    }

    @Test
    void createAuthor_shouldReturn400andValidationError() throws Exception {

        record TestCase(
                String desc,
                String json,
                ResultMatcher statusCode,
                List<ResultMatcher> matchers
        ) {
            public ResultMatcher[] combineWith(ResultMatcher... commonMatchers) {
                return Stream.concat(
                        Arrays.stream(commonMatchers),
                        this.matchers.stream()
                ).toArray(ResultMatcher[]::new);
            }
        }

        List<TestCase> testCases = List.of(
                // firstname tests
                new TestCase(
                        "on empty firstname expect error",
                        "{\"lastname\":\"123\", \"surname\":\"123\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.firstname").value("firstname is required"))
                ),
                new TestCase(
                        "on long firstname expect error",
                        String.format("{\"firstname\":\"%s\", \"lastname\":\"123\", \"lastname\":\"123\"}", "a".repeat(260)),
                        status().isBadRequest(),
                        List.of(jsonPath("*.firstname").value("firstname must be between 2 and 255 characters"))
                ),
                new TestCase(
                        "on short firstname expect error",
                        "{\"firstname\":\"1\", \"lastname\":\"123\", \"lastname\":\"123\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.firstname").value("firstname must be between 2 and 255 characters"))
                ),

                // surname tests
                new TestCase(
                        "on empty surname expect error",
                        "{\"firstname\":\"123\", \"lastname\":\"123\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.surname").value("surname is required"))
                ),
                new TestCase(
                        "on long surname expect error",
                        String.format("{\"firstname\":\"123\", \"lastname\":\"123\", \"surname\":\"%s\"}", "a".repeat(260)),
                        status().isBadRequest(),
                        List.of(jsonPath("*.surname").value("surname must be between 2 and 255 characters"))
                ),
                new TestCase(
                        "on short surname expect error",
                        "{\"firstname\":\"132\", \"lastname\":\"123\", \"surname\":\"1\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.surname").value("surname must be between 2 and 255 characters"))
                ),

                // lastname tests
                new TestCase(
                        "on empty lastname expect error",
                        "{\"firstname\":\"123\", \"surname\":\"123\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.lastname").value("lastname is required"))
                ),
                new TestCase(
                        "on long lastname expect error",
                        String.format("{\"firstname\":\"123\", \"lastname\":\"%s\", \"surname\":\"123\"}", "a".repeat(260)),
                        status().isBadRequest(),
                        List.of(jsonPath("*.lastname").value("lastname must be between 2 and 255 characters"))
                ),
                new TestCase(
                        "on short lastname expect error",
                        "{\"firstname\":\"123\", \"lastname\":\"1\", \"surname\":\"123\"}",
                        status().isBadRequest(),
                        List.of(jsonPath("*.lastname").value("lastname must be between 2 and 255 characters"))
                )
        );


        testCases.forEach(tc -> {
            try {
                mockMvc.perform(
                                post(BASE_API_AUTHOR_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.json))
                        .andExpect(tc.statusCode)
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(BASE_API_AUTHOR_PATH)
                                )
                        );
            } catch (Exception e) {
                throw new RuntimeException("Failed test: " + tc.desc, e);
            }
        });
    }

    @Test
    void getAllAuthors_shouldReturn200andTwoAuthors() throws Exception {
        final String JSON_PATH_VALID_AUTHOR_1 = "/requests/create-valid-author.json";
        final String JSON_PATH_VALID_AUTHOR_2 = "/requests/create-valid-author2.json";

        String firstAuthor = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + JSON_PATH_VALID_AUTHOR_1);
        String secondAuthor = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + JSON_PATH_VALID_AUTHOR_2);

        mockMvc.perform(
                        post(BASE_API_AUTHOR_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstAuthor))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post(BASE_API_AUTHOR_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondAuthor))
                .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_API_AUTHOR_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authors", hasSize(2)))
                .andExpectAll(
                        jsonPath("$.authors[*].id", everyItem(not(empty()))),
                        jsonPath("$.authors[*].firstname", everyItem(not(emptyString()))),
                        jsonPath("$.authors[*].surname", everyItem(not(emptyString()))),
                        jsonPath("$.authors[*].lastname", everyItem(not(emptyString()))),
                        jsonPath("$.authors[*].created_at", everyItem(not(empty()))),
                        jsonPath("$.authors[*].updated_at", everyItem(not(empty()))));
    }

    @Test
    void deleteAuthor_shouldReturn200() throws Exception {
        String requestBody = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + "/requests/create-valid-author.json");

        MvcResult res = mockMvc.perform(
                        post(BASE_API_AUTHOR_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = res.getResponse().getHeader("Location");
        assertNotNull(location);
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        final String ID_PATH = BASE_API_AUTHOR_PATH + id;

        mockMvc.perform(
                        get(ID_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id));

        mockMvc.perform(
                        delete(ID_PATH)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(ID_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
