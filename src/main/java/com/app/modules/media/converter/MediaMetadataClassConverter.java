package com.app.modules.media.converter;

import com.app.modules.media.metadata.MediaMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = true)
@Slf4j
public class MediaMetadataClassConverter implements AttributeConverter<MediaMetadata, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    static {
        OBJECT_MAPPER.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.app.modules.media.metadata")
                        .allowIfBaseType(MediaMetadata.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
    }

    @Override
    public String convertToDatabaseColumn(MediaMetadata attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("failed to serialize MediaMetadata", e);
            throw new RuntimeException("failed to serialize MediaMetadata", e);
        }
    }

    @Override
    public MediaMetadata convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, MediaMetadata.class);
        } catch (JsonProcessingException e) {
            log.error("failed to deserialize MediaMetadata", e);
            throw new RuntimeException("failed to deserialize MediaMetadata", e);
        }
    }

}
