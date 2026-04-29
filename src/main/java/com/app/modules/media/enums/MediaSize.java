package com.app.modules.media.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaSize {
    ORIGINAL("original"),
    LARGE("large"),
    MEDIUM("medium"),
    SMALL("small"),
    THUMBNAIL("thumbnail"),
    ICON("icon"),
    CUSTOM("custom");

    private final String code;

    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code.toLowerCase();
    }
}
