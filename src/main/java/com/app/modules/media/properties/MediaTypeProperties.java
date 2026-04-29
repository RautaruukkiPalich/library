package com.app.modules.media.properties;

import lombok.NonNull;

import java.util.Map;
import java.util.Set;

public record MediaTypeProperties(
        long minSize,
        long maxSize,
        Set<String> extensions,
        Set<String> contentTypes,
        Map<String, String> extensionToContentType,
        String targetExtension,
        String targetContentType
) {
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public static final MediaTypeProperties IMAGE = new MediaTypeProperties(
            1L,
            10 * 1024 * 1024L,
            Set.of("jpg", "jpeg", "png", "gif", "webp"),
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp"),
            Map.of(
                    "jpg", "image/jpeg",
                    "jpeg", "image/jpeg",
                    "png", "image/png",
                    "gif", "image/gif",
                    "webp", "image/webp"),
            "webp",
            "image/webp"
    );
    public static final MediaTypeProperties VIDEO = new MediaTypeProperties(
            1L,
            20 * 1024 * 1024L,
            Set.of("mp4", "avi", "mov", "mkv"),
            Set.of("video/mp4", "video/x-msvideo", "video/quicktime", "video/x-matroska"),
            Map.of(
                    "mp4", "video/mp4",
                    "avi", "video/x-msvideo",
                    "mov", "video/quicktime",
                    "mkv", "video/x-matroska"
            ),
            "mp4", "video/mp4"
    );

    public String getContentType(@NonNull String extension) {
        return this.extensionToContentType.getOrDefault(
                extension,
                DEFAULT_CONTENT_TYPE
        );
    }
}