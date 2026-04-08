package com.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

public abstract class BaseTest {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T unmarshall(@NonNull String jsonString, @NonNull Class<T> targetClass) {
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
