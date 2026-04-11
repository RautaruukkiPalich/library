package com.app.modules.media.enums;

import com.app.modules.media.utils.FileValidator;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Getter
public enum MediaContentType implements CodeBasedEnum {
    IMAGE(
            "image",
            1L,
            10 * 1024 * 1024L,
            Map.of(
                    "jpeg", "image/jpeg",
                    "jpg", "image/jpeg",
                    "png", "image/png",
                    "gif", "image/gif",
                    "webp", "image/webp"),
            "webp", "image/webp"
    ) {
        @Override
        public void validate(@NonNull MultipartFile file) {
            FileValidator.baseValidation(file);
            FileValidator.validateImageFile(file, this);
        }
    },
    VIDEO(
            "video",
            1L,
            20 * 1024 * 1024L,
            Map.of(
                    "mp4", "video/mp4",
                    "avi", "video/x-msvideo",
                    "mov", "video/quicktime",
                    "mkv", "video/x-matroska"
            ),
            "mp4", "video/mp4"
    ) {
        @Override
        public void validate(@NonNull MultipartFile file) {
            FileValidator.baseValidation(file);
        }
    };

    private final String code;
    private final Long minSize;
    private final Long maxSize;
    private final Map<String, String> extensionMap;
    private final Set<String> extensions;
    private final Set<String> contentTypes;
    private final String targetExt;
    private final String targetContentType;

    MediaContentType(String code, Long minSize, Long maxSize, Map<String, String> extensionMap,
                     String targetConvertExtension, String targetConvertContentType) {
        this.code = code;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.extensionMap = extensionMap;
        this.extensions = Set.copyOf(extensionMap.keySet());
        this.contentTypes = Set.copyOf(extensionMap.values());
        this.targetExt = targetConvertExtension;
        this.targetContentType = targetConvertContentType;
    }

    public static MediaContentType fromCode(String code) throws IllegalArgumentException {
        return CodeBasedEnum.fromCode(MediaContentType.class, code);
    }

    @Override
    public String toString() {
        return getPreparedCode();
    }

    public abstract void validate(@NonNull MultipartFile file);
}
