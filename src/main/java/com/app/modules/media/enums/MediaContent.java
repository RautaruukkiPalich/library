package com.app.modules.media.enums;

import com.app.core.enums.BaseEnum;
import com.app.modules.media.properties.MediaTypeProperties;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum MediaContent implements BaseEnum {
    IMAGE(MediaTypeProperties.IMAGE),
    VIDEO(MediaTypeProperties.VIDEO);

    private final MediaTypeProperties props;

    @JsonCreator
    public static MediaContent fromName(String name) {
        return BaseEnum.fromName(MediaContent.class, name);
    }

    public static MediaContent fromNameOrThrow(String name) {
        return BaseEnum.fromNameOrThrow(MediaContent.class, name);
    }

    @Override
    public String toString() {
        return BaseEnum.normalize(this.name());
    }

    public static MediaContent fromContentType(@NonNull String contentType) throws IllegalArgumentException {
        String preparedContentType = lowerCaseTrimString(contentType);

        if (preparedContentType.startsWith("image/")) return IMAGE;
        if (preparedContentType.startsWith("video/")) return VIDEO;
        throw new IllegalArgumentException("unsupported content type: " + contentType);
    }

    public static MediaContent fromExtension(@NonNull String extension) {
        String preparedExtension = lowerCaseTrimString(extension);
        if (IMAGE.getProps().extensions().contains(preparedExtension)) return IMAGE;
        if (VIDEO.getProps().extensions().contains(preparedExtension)) return VIDEO;
        throw new IllegalArgumentException("unsupported extension: " + preparedExtension);
    }

    private static String lowerCaseTrimString(String s) {
        return s == null ? "" : s.toLowerCase().trim();
    }


}
