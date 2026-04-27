package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.modules.media.controller.MediaControllerDTO;
import com.app.utils.RegisterLoginHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.security.enabled=true",
        "logging.level.root=INFO",
        "logging.level.com.app=INFO",
        "logging.level.org.springframework=INFO"
})
public class MediaControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;

    RegisterLoginHelper loginHelper;

    private final static String USER_FIRSTNAME = "firstname";
    private final static String USER_LASTNAME = "lastname";
    private final static String USER_SURNAME = "surname";
    private final static String USER_EMAIL = "test@test.com";
    private final static String USER_PASSWORD = "QWErty123";

    private final static String MEDIA_API_PATH = "/api/media";

    @AfterEach
    void afterEach() {
        try {
            cleanupMediaStorage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
    }

    void cleanupDatabase() {
        truncate(jdbcTemplate, "users", "refresh_tokens");
    }

    @Test
    void testUploadFile() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String uploadBody = mockMvc.perform(
                        multipart(HttpMethod.POST, MEDIA_API_PATH + "/upload")
                                .header("Authorization", "Bearer %s".formatted(accessToken))
                                .file(testFile))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.media_uuid").isNotEmpty())
                .andReturn()
                .getResponse().getContentAsString();

        var mediaUUID = unmarshall(uploadBody, MediaControllerDTO.Response.MediaUUID.class).getMediaUuid();

        mockMvc.perform(get(MEDIA_API_PATH + "/%s".formatted(mediaUUID))
                        .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.media_uuid").isNotEmpty())
                .andExpect(jsonPath("$.user_id").isNotEmpty())
                .andExpect(jsonPath("$.original_filename").isNotEmpty())
                .andExpect(jsonPath("$.sizes").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].content_type").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].media_size").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].download_url").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].metadata").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].metadata.width").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].metadata.height").isNotEmpty())
                .andExpect(jsonPath("$.sizes[0].metadata.file_size").isNotEmpty());

        mockMvc.perform(delete("%s/%s".formatted(MEDIA_API_PATH, mediaUUID))
                        .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isNoContent());
    }
}
