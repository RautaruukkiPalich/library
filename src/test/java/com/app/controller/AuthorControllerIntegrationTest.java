package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.utils.JsonTestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

public class AuthorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String PREFIX_JSON_PATH = "/controllers/author/json";

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
        String requestBody = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + "/requests/create-valid-author.json");

        MvcResult res = mockMvc.perform(
                post("/api/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = res.getResponse().getHeader("Location");
        assertNotNull(location);
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(
                get("/api/authors/" + id)
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
            String json,
            ResultMatcher statusCode,
            List<ResultMatcher> matchers
        ){
            public ResultMatcher[] combineWith(ResultMatcher... commonMatchers) {
                return Stream.concat(
                    Arrays.stream(commonMatchers),
                    this.matchers.stream()
                ).toArray(ResultMatcher[]::new);
            }
        }

        List<TestCase> testCases = List.of(
            // firstname tests

            // empty firstname
            new TestCase(
                "{\"lastname\":\"123\", \"surname\":\"123\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.firstname").value("firstname is required"))
            ),
            // long firstname
            new TestCase(
                String.format("{\"firstname\":\"%s\", \"lastname\":\"123\", \"lastname\":\"123\"}", "a".repeat(260) ),
                status().isBadRequest(),
                List.of(jsonPath("*.firstname").value("firstname must be between 2 and 255 characters"))
            ),
            // short firstname
            new TestCase(
                "{\"firstname\":\"1\", \"lastname\":\"123\", \"lastname\":\"123\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.firstname").value("firstname must be between 2 and 255 characters"))
            ),
            // surname tests

            // empty surname
            new TestCase(
                "{\"firstname\":\"123\", \"lastname\":\"123\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.surname").value("surname is required"))
            ),
            // long surname
            new TestCase(
                String.format("{\"firstname\":\"123\", \"lastname\":\"123\", \"surname\":\"%s\"}", "a".repeat(260) ),
                status().isBadRequest(),
                List.of(jsonPath("*.surname").value("surname must be between 2 and 255 characters"))
            ),
            // short surname
            new TestCase(
                "{\"firstname\":\"132\", \"lastname\":\"123\", \"surname\":\"1\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.surname").value("surname must be between 2 and 255 characters"))
            ),
            // lastname tests

            // empty lastname
            new TestCase(
                "{\"firstname\":\"123\", \"surname\":\"123\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.lastname").value("lastname is required"))
            ),
            // long lastname
            new TestCase(
                String.format("{\"firstname\":\"123\", \"lastname\":\"%s\", \"surname\":\"123\"}", "a".repeat(260) ),
                status().isBadRequest(),
                List.of(jsonPath("*.lastname").value("lastname must be between 2 and 255 characters"))
            ),
            // short lastname
            new TestCase(
                "{\"firstname\":\"123\", \"lastname\":\"1\", \"surname\":\"123\"}",
                status().isBadRequest(),
                List.of(jsonPath("*.lastname").value("lastname must be between 2 and 255 characters"))
            )
        );
        

        testCases.forEach(tc -> {
            try {
                mockMvc.perform(
                    post("/api/authors/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.json))
                    .andExpect(tc.statusCode)
                    .andExpectAll(
                        tc.combineWith(
                            jsonPath("error").value("validation error"),
                            jsonPath("message").value("invalid argument parameter"),
                            jsonPath("path").value("/api/authors/")
                        )
                    );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void getAllAuthors_shouldReturn200andTwoAuthors() throws Exception {

        String firstAuthor = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + "/requests/create-valid-author.json");
        String secondAuthor = JsonTestUtils.readJsonFile(PREFIX_JSON_PATH + "/requests/create-valid-author2.json");

        mockMvc.perform(
                post("/api/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstAuthor))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondAuthor))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/authors/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authors", hasSize(2)))
                .andExpectAll(
                        jsonPath("$.authors[*].id", everyItem(not(empty()))),
                        // jsonPath("$.authors[*].id", containsInAnyOrder(1, 2)),
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
                post("/api/authors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = res.getResponse().getHeader("Location");
        assertNotNull(location);
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        mockMvc.perform(
                get("/api/authors/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id));

        mockMvc.perform(
            delete("/api/authors/" + id)
        )
        .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/api/authors/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
