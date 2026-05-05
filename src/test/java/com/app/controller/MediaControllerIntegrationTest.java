package com.app.controller;

import com.app.BaseIntegrationTest;
import com.app.modules.media.controller.MediaControllerDTO;
import com.app.utils.RegisterLoginHelper;
import lombok.SneakyThrows;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private final int EXPECTED_TOTAL_SIZES = 6;
    private final int CONVERSION_TIMEOUT_SECONDS = 30;
    private final int POLLING_INTERVAL_SECONDS = 2;

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
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            UUID mediaUuid = uploadFile();

            var mediaItem = getMediaItem(mediaUuid);

            assertThat(mediaItem).isNotNull();

            deleteMedia(mediaUuid);
        });
    }

    @Test
    void testDownloadFiles() throws Exception {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            UUID mediaUuid = uploadFile();
            var mediaItem = getMediaItem(mediaUuid);
            var urls = mediaItem.getSizes().stream()
                    .flatMap(s -> s.getDownloadUrl().stream())
                    .toList();

            assertThat(urls).hasSizeGreaterThanOrEqualTo(2);

            urls.forEach(this::downloadFile);

            deleteMedia(mediaUuid);
        });
    }

    @Test
    void testDownloadConverts() throws Exception {
        assertTimeoutPreemptively(Duration.ofSeconds(45), () -> {
            UUID mediaUuid = uploadFile();

            var urls = waitForConversionComplete(mediaUuid,
                    (item) -> item.getTotalCount() != null && item.getTotalCount() == EXPECTED_TOTAL_SIZES,
                    POLLING_INTERVAL_SECONDS,
                    CONVERSION_TIMEOUT_SECONDS);

            urls.forEach(this::downloadFile);

            deleteMedia(mediaUuid);
        });
    }

    @SneakyThrows
    private UUID uploadFile() {
        byte[] content = Files.readAllBytes(Path.of("src/test/resources/controllers/files/white_rectangle.png"));
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                content
        );

        String uploadBody = mockMvc.perform(
                        multipart(HttpMethod.POST, MEDIA_API_PATH + "/upload")
                                .header("Authorization", "Bearer %s".formatted(accessToken))
                                .file(testFile))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.media_uuid").isNotEmpty())
                .andReturn()
                .getResponse().getContentAsString();

        return unmarshall(uploadBody, MediaControllerDTO.Response.MediaUUID.class).getMediaUuid();
    }

    @SneakyThrows
    private MediaControllerDTO.Response.MediaItem getMediaItem(UUID mediaUuid) {
        var mediaItemResp = mockMvc.perform(get(MEDIA_API_PATH + "/%s".formatted(mediaUuid))
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
                .andExpect(jsonPath("$.sizes[0].metadata.file_size").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        return unmarshall(mediaItemResp, MediaControllerDTO.Response.MediaItem.class);
    }

    @SneakyThrows
    private void deleteMedia(UUID mediaUuid) {
        mockMvc.perform(delete("%s/%s".formatted(MEDIA_API_PATH, mediaUuid))
                        .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isNoContent());
        Thread.sleep(1000);
    }

    @SneakyThrows
    private void downloadFile(String url) {
        mockMvc.perform(get(url)
                        .header("Authorization", "Bearer %s".formatted(accessToken)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().exists("Content-Disposition"));
    }

    private List<String> waitForConversionComplete(
            UUID mediaUuid,
            Predicate<MediaControllerDTO.Response.MediaItem> predicate,
            int pollingInterval,
            int timeoutSeconds) {
        long startTime = System.currentTimeMillis();

        while (true) {
            MediaControllerDTO.Response.MediaItem item = getMediaItem(mediaUuid);
            if (predicate.test(item)) {
                return item.getSizes().stream()
                        .flatMap(sizeInfo -> sizeInfo.getDownloadUrl().stream())
                        .toList();
            }

            if (System.currentTimeMillis() - startTime >= timeoutSeconds * 1000L) {
                throw new RuntimeException("Conversion timeout for media: " + mediaUuid);
            }

            try {
                Thread.sleep(pollingInterval * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("waiting interrupted", e);
            }

        }
    }
}
