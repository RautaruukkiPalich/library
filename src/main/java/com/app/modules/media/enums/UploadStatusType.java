package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UploadStatusType {
    PENDING(0),
    PROCESSING(1),
    CANCELLED(2),
    FAILED(3),
    COMPLETED(4);

    private final int code;

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isError() {
        return this == FAILED;
    }

    public boolean isSameOrHigher(UploadStatusType status) {
        return this.code >= status.code;
    }

    public static UploadStatusType fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.getCode() == code)
                .findFirst()
                .orElse(PENDING);
    }
}
