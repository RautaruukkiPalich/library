package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaPurpose {
    USER_AVATAR(EntityType.USER);

    private final EntityType entityType;
}
