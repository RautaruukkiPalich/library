package com.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonTestUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    public static String readJsonFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    
    public static <T> T readJsonFile(String path, Class<T> valueType) throws IOException {
        String content = readJsonFile(path);
        return objectMapper.readValue(content, valueType);
    }
    
    public static String asJsonString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }
}