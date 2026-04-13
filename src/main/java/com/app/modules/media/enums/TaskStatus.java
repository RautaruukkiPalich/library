package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    PENDING(0, "pending"),
    PROCESSING(1, "processing"),
    CANCELLED(2, "cancelled"),
    FAILED(3, "failed"),
    COMPLETED(4, "completed");

    private final int code;
    private final String value;

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isError() {
        return this == FAILED;
    }

    public boolean isSameOrHigher(TaskStatus status) {
        return this.code >= status.code;
    }

    public static TaskStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.getCode() == code)
                .findFirst()
                .orElse(PENDING);
    }

    public static TaskStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String finalValue = value.toLowerCase().trim();

        return Arrays.stream(values())
                .filter(status -> status.getValue().equalsIgnoreCase(finalValue))
                .findFirst()
                .orElse(null);
    }
}
