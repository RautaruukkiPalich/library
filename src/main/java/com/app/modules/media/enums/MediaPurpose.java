package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaPurpose implements CodeBasedEnum {
    USER_AVATAR("user_avatar", EntityType.USER);

    private final String code;
    private final EntityType entityType;

    public static MediaPurpose fromCode(String code) {
        return CodeBasedEnum.fromCode(MediaPurpose.class, code);
    }

    @Override
    public String toString() {
        return CodeBasedEnum.preparedCode(this.code);
    }
}
