package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.utils.IntegrationTestCase;
import com.app.utils.RegisterLoginHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "app.security.enabled=true")
public class AuthControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;
    private String refreshToken;

    RegisterLoginHelper loginHelper;

    private final static String USER_FIRSTNAME = "firstname";
    private final static String USER_LASTNAME = "lastname";
    private final static String USER_SURNAME = "surname";
    private final static String USER_EMAIL = "test@test.com";
    private final static String USER_PASSWORD = "QWErty123";

    private final static String BASE_API_AUTH_PATH = "/api/auth";

    private final static String refreshTokenTmpl = "{\"refresh_token\":\"%s\"}";
    private final static String resetPasswordTmpl = "{\"email\":\"%s\"}";

    @BeforeEach
    void setUp() throws Exception {
        cleanupDatabase();

        loginHelper = RegisterLoginHelper.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .firstname(USER_FIRSTNAME)
                .lastname(USER_LASTNAME)
                .surname(USER_SURNAME)
                .mockMvc(mockMvc)
                .build();

        loginHelper.registerUser();
        loginHelper.loginUser();
        accessToken = loginHelper.getAccessToken();
        refreshToken = loginHelper.getRefreshToken();
    }

    void cleanupDatabase() {
        truncate(jdbcTemplate, "users", "refresh_tokens");
    }

    @TestFactory
    Stream<DynamicTest> refreshTokensTest() throws Exception {
        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on valid refresh token expect no error")
                        .jsonBody(refreshTokenTmpl.formatted(refreshToken))
                        .statusMatcher(status().isOk())
                        .matchers(List.of(
                                jsonPath("$.access_token").isNotEmpty(),
                                jsonPath("$.access_token").isString(),
                                jsonPath("$.refresh_token").isNotEmpty(),
                                jsonPath("$.refresh_token").isString()
                        ))
                        .build(),
                IntegrationTestCase.builder()
                        .desc("on revoked refresh token expect error")
                        .jsonBody(refreshTokenTmpl.formatted(refreshToken))
                        .statusMatcher(status().is(HttpStatus.UNAUTHORIZED.value()))
                        .matchers(List.of(
                                jsonPath("$.access_token").doesNotExist(),
                                jsonPath("$.refresh_token").doesNotExist()
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () -> mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/refresh-tokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                .andExpect(tc.statusMatcher())
                .andExpectAll(tc.combineWith())));
    }

    @TestFactory
    Stream<DynamicTest> resetPasswordTest() throws Exception {
        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on valid email expect no error")
                        .jsonBody(resetPasswordTmpl.formatted(USER_EMAIL))
                        .statusMatcher(status().isOk())
                        .matchers(List.of())
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on invalid email expect error")
                        .jsonBody(resetPasswordTmpl.formatted("gg" + USER_EMAIL))
                        .statusMatcher(status().is(HttpStatus.UNAUTHORIZED.value()))
                        .matchers(List.of())
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () -> mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                .andExpect(tc.statusMatcher())
                .andExpectAll(tc.combineWith())));
    }

    @Test
    void revokeTokensTest() throws Exception {

        mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/revoke-tokens")
                                .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/refresh-tokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(refreshTokenTmpl.formatted(refreshToken)))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }
}
