package com.app.modules.media.enums;

import com.app.modules.media.properties.MediaTypeProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum MediaContent implements CodeBasedEnum {
    IMAGE("image", MediaTypeProperties.IMAGE),
    VIDEO("video", MediaTypeProperties.VIDEO);

    private final String code;
    private final MediaTypeProperties props;

    public static MediaContent fromCode(String code) throws IllegalArgumentException {
        return CodeBasedEnum.fromCode(MediaContent.class, code);
    }

    public static MediaContent fromContentType(@NonNull String contentType) throws IllegalArgumentException {
        String normalizedContentType = contentType.toLowerCase().strip();

        if (normalizedContentType.startsWith("image/")) return IMAGE;
        if (normalizedContentType.startsWith("video/")) return VIDEO;
        throw new IllegalArgumentException("unsupported content type: " + contentType);
    }

    @Override
    public String toString() {
        return getPreparedCode();
    }

    @Override
    public String getCode() {
        return code;
    }
}
