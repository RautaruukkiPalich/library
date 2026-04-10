package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MediaSize {
    ORIGINAL("original", 0, 0, true, false),
    LARGE("large", 1920, 1080, true, false),
    MEDIUM("medium", 800, 600, true, false),
    SMALL("small", 400, 300, true, false),
    THUMBNAIL("thumbnail", 150, 150, false, true),
    ICON("icon", 50, 50, false, true);

    private final String code;
    private final int width;
    private final int height;
    private final boolean keepAspectRatio;
    private final boolean cropToSquare;

    public static MediaSize fromCode(String code) {
        return Arrays.stream(values())
                .filter(size -> size.getCode().equals(preparedCode(code)))
                .findFirst()
                .orElse(ORIGINAL);
    }

    @Override
    public String toString() {
        return preparedCode(this.code);
    }

    public static final List<MediaSize> sizesToConvert = List.of(
            ICON,
            THUMBNAIL,
            SMALL,
            MEDIUM,
            LARGE
    );

    private static String preparedCode(String code) {
        return code.toLowerCase().strip();
    }
}
