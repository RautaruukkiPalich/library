package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum MediaSize implements CodeBasedEnum {
    ORIGINAL("original", 0, 0, true, false),
    LARGE("large", 1920, 1080, true, false),
    MEDIUM("medium", 800, 600, true, false),
    SMALL("small", 400, 300, true, false),
    THUMBNAIL("thumbnail", 150, 150, false, true),
    ICON("icon", 50, 50, false, true),
    CUSTOM("custom", 0, 0, true, false);

    private final String code;
    private final int width;
    private final int height;
    private final boolean keepAspectRatio;
    private final boolean cropToSquare;

    public static MediaSize fromCode(String code) {
        return CodeBasedEnum.fromCode(MediaSize.class, code);
    }

    public static MediaSize fromCodeOrDefault(String code) {
        return CodeBasedEnum.fromCode(MediaSize.class, code, MediaSize.ORIGINAL);
    }

    public static final Set<MediaSize> DEFAULT_MEDIA_SIZE = Set.of(
            ICON,
            THUMBNAIL,
            SMALL,
            MEDIUM,
            LARGE
    );

    @Override
    public String toString() {
        return getPreparedCode();
    }
}
