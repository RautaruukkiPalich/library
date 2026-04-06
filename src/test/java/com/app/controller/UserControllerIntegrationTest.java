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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        mockMvc.perform(put(BASE_API_USER_PATH + "/me/password")
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

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/password")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordTmpl.formatted(wrongPassword, NEW_PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(put(BASE_API_USER_PATH + "/me/password")
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

        UserControllerDTO.Response.PublicProfile deserialized = objectMapper
                .readValue(respBody, UserControllerDTO.Response.PublicProfile.class);

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


    @Test
    void editFirstname_shouldReturn200() throws Exception {
        String newFirstname = "AlexanderUpdated";
        String jsonBody = "{\"firstname\":\"%s\"}".formatted(newFirstname);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/firstname")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value(newFirstname));
    }

    @Test
    void editFirstname_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(put(BASE_API_USER_PATH + "/me/firstname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"NewName\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editProfileFields_shouldReturn401_whenInvalidToken() throws Exception {
        String jsonBody = "{\"firstname\":\"NewName\"}";

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/firstname")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editLastname_shouldReturn200() throws Exception {
        String newLastname = "PushkinUpdated";
        String jsonBody = "{\"lastname\":\"%s\"}".formatted(newLastname);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/lastname")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname").value(newLastname));
    }

    @Test
    void editSurname_shouldReturn200() throws Exception {
        String newSurname = "SergeevichUpdated";
        String jsonBody = "{\"surname\":\"%s\"}".formatted(newSurname);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/surname")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value(newSurname));
    }

    @Test
    void editEmail_shouldReturn200() throws Exception {
        String newEmail = "updated@test.com";
        String jsonBody = "{\"email\":\"%s\"}".formatted(newEmail);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/email")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_API_USER_PATH + "/me")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    void editEmail_shouldReturn400_whenEmailInvalid() throws Exception {
        String invalidEmail = "invalid-email";
        String jsonBody = "{\"email\":\"%s\"}".formatted(invalidEmail);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/email")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editEmail_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        String secondEmail = "second@test.com";
        RegisterLoginHelper secondUserHelper = RegisterLoginHelper.builder()
                .email(secondEmail)
                .password(USER_PASSWORD)
                .firstname("Second")
                .lastname("User")
                .surname("Test")
                .mockMvc(mockMvc)
                .build();

        secondUserHelper.registerUser();

        String jsonBody = "{\"email\":\"%s\"}".formatted(secondEmail);

        mockMvc.perform(put(BASE_API_USER_PATH + "/me/email")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict());
    }
}