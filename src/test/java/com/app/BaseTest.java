package com.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public abstract class BaseTest {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${storage.local.path:1test_local_media_storage}")
    private String localStoragePath;

    protected void cleanupMediaStorage() throws IOException {
        Path dir = Paths.get(localStoragePath).normalize();
        if (!Files.exists(dir)) {
            return;
        }

        try (var stream = Files.walk(dir)) {
            stream
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("failed to delete: " + path + " | cause: " + e);
                        }
                    });
        }
    }

    protected <T> T unmarshall(@NonNull String jsonString, @NonNull Class<T> targetClass) {
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
