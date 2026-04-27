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
        String preparedContentType = CodeBasedEnum.preparedCode(contentType);

        if (preparedContentType.startsWith("image/")) return IMAGE;
        if (preparedContentType.startsWith("video/")) return VIDEO;
        throw new IllegalArgumentException("unsupported content type: " + contentType);
    }

    public static MediaContent fromExtension(@NonNull String extension) {
        String preparedExtension = CodeBasedEnum.preparedCode(extension);
        if (IMAGE.getProps().extensions().contains(preparedExtension)) return IMAGE;
        if (VIDEO.getProps().extensions().contains(preparedExtension)) return VIDEO;
        throw new IllegalArgumentException("unsupported extension: " + preparedExtension);
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
