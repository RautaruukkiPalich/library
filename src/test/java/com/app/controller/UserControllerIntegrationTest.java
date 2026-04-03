package com.app.controller;


import com.app.BaseIntegrationTest;
import com.app.modules.user.dto.UserControllerDTO;
import com.app.utils.RegisterLoginHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "app.security.enabled=true")
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;

    private final static String USER_FIRSTNAME = "Alexander";
    private final static String USER_LASTNAME = "Pushkin";
    private final static String USER_SURNAME = "Sergeevich";
    private final static String USER_EMAIL = "test@test.com";
    private final static String USER_PASSWORD = "QWErty123";
    private final static String NEW_PASSWORD = "NewPassword123";

    private final static String BASE_API_AUTH_PATH = "/api/auth";
    private final static String BASE_API_USER_PATH = "/api/users";

    private final static String changePasswordTmpl = "{\"old_password\":\"%s\", \"password\":\"%s\"}";

    @BeforeEach
    void setUp() throws Exception {
        cleanupDatabase();

        RegisterLoginHelper loginHelper = RegisterLoginHelper.builder()
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
    }

    void cleanupDatabase() {
        truncate(jdbcTemplate, "users", "refresh_tokens");
    }

    @Test
    void getUserById_shouldReturn404_whenUserNotFound() throws Exception {
        long nonExistentId = 99999L;

        mockMvc.perform(get(BASE_API_USER_PATH + "/" + nonExistentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMe_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstname").value(USER_FIRSTNAME))
                .andExpect(jsonPath("$.lastname").value(USER_LASTNAME))
                .andExpect(jsonPath("$.surname").value(USER_SURNAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.role").isNotEmpty());
    }

    @Test
    void getMe_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMe_shouldReturn401_whenInvalidToken() throws Exception {
        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer invalid-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_shouldReturn200() throws Exception {
        mockMvc.perform(post(BASE_API_USER_PATH + "/me/change-password")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordTmpl.formatted(USER_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isOk());

        String loginJson = "{\"email\":\"%s\", \"password\":\"%s\"}".formatted(USER_EMAIL, NEW_PASSWORD);

        mockMvc.perform(post(BASE_API_AUTH_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty());
    }

    @Test
    void changePassword_shouldReturn400_whenWrongOldPassword() throws Exception {
        String wrongPassword = "WrongPassword123";

        mockMvc.perform(post(BASE_API_USER_PATH + "/me/change-password")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordTmpl.formatted(wrongPassword, NEW_PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(post(BASE_API_USER_PATH + "/me/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordTmpl.formatted(USER_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {
        String respBody = mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();

        UserControllerDTO.PublicResponse deserialized = objectMapper
                .readValue(respBody, UserControllerDTO.PublicResponse.class);

        mockMvc.perform(get(BASE_API_USER_PATH + "/" + deserialized.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deserialized.getId()))
                .andExpect(jsonPath("$.firstname").value(USER_FIRSTNAME))
                .andExpect(jsonPath("$.lastname").value(USER_LASTNAME))
                .andExpect(jsonPath("$.surname").value(USER_SURNAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.role").isNotEmpty());
    }

}