package com.app.modules.media.enums;

import com.app.core.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaSize implements BaseEnum {
    ORIGINAL, LARGE, MEDIUM, SMALL, THUMBNAIL, ICON, CUSTOM;

    @JsonCreator
    public static MediaSize fromName(String name) {
        return BaseEnum.fromName(MediaSize.class, name);
    }

    public static MediaSize fromNameOrThrow(String name) {
        return BaseEnum.fromNameOrThrow(MediaSize.class, name);
    }

    @Override
    public String toString() {
        return BaseEnum.normalize(this.name());
    }
}
