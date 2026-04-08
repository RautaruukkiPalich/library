package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.modules.media.dto.MediaControllerDTO;
import com.app.utils.RegisterLoginHelper;
import org.hamcrest.CoreMatchers;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        final String PATH = MEDIA_API_PATH + "/users/avatar";

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String body = mockMvc.perform(
                        multipart(HttpMethod.POST, PATH)
                                .header("Authorization", "Bearer %s".formatted(accessToken))
                                .contentType(MediaType.IMAGE_JPEG)
                                .file(testFile))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.task_uuid").isNotEmpty())
                .andExpect(jsonPath("$.status").isNotEmpty())
                .andExpect(jsonPath("$.status_check_url").isNotEmpty())
                .andExpect(jsonPath("$.status_check_url", CoreMatchers.containsString("/api/media/tasks/")))
                .andReturn()
                .getResponse().getContentAsString();

        var unmarshalled = unmarshall(body, MediaControllerDTO.Response.MediaUploadTaskStatus.class);

        mockMvc.perform(get(unmarshalled.getStatusCheckUrl()).header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task_uuid").isNotEmpty())
                .andExpect(jsonPath("$.status").isNotEmpty());
    }
}
