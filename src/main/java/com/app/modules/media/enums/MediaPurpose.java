package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MediaPurpose {
    USER_AVATAR("user_avatar", EntityType.USER);

    private final String code;
    private final EntityType entityType;

    public static MediaPurpose fromCode(String code) {
        return Arrays.stream(values())
                .filter(mp -> mp.getCode().equals(preparedCode(code)))
                .findFirst()
                .orElse(USER_AVATAR);
    }

    @Override
    public String toString() {
        return preparedCode(this.code);
    }

    private static String preparedCode(String code) {
        return code.toLowerCase().strip();
    }
}
