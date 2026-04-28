package com.app.modules.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final EnumSet<TaskStatus> FINAL_STATUSES = EnumSet.of(COMPLETED, FAILED, CANCELLED);
    private static final EnumSet<TaskStatus> ERROR_STATUSES = EnumSet.of(FAILED);
    private static final EnumSet<TaskStatus> ACTIVE_STATUSES = EnumSet.of(PENDING, PROCESSING);

    private static final Map<Integer, TaskStatus> BY_CODE = new ConcurrentHashMap<>();
    private static final Map<String, TaskStatus> BY_VALUE = new ConcurrentHashMap<>();

    static {
        for (TaskStatus status : values()) {
            BY_CODE.put(status.code, status);
            BY_VALUE.put(normalizeString(status.value), status);
        }
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean isFinal() {
        return FINAL_STATUSES.contains(this);
    }

    public boolean isError() {
        return ERROR_STATUSES.contains(this);
    }

    public boolean isActive() {
        return ACTIVE_STATUSES.contains(this);
    }

    public boolean canTransitionTo(TaskStatus target) {
        if (target == null) return false;
        if (this == target) return true;

        return switch (this) {
            case PENDING -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == COMPLETED || target == FAILED || target == CANCELLED;
            case FAILED -> target == PENDING;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Optional<TaskStatus> fromCode(int code) {
        return Optional.ofNullable(BY_CODE.get(code));
    }

    public static Optional<TaskStatus> fromValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_VALUE.get(normalizeString(value)));
    }

    public static TaskStatus fromCode(int code, TaskStatus defaultValue) {
        return fromCode(code).orElse(defaultValue);
    }

    public static TaskStatus fromValue(String value, TaskStatus defaultValue) {
        return fromValue(value).orElse(defaultValue);
    }

    private static String normalizeString(String str) {
        return str == null ? null : str.toLowerCase().trim();
    }
}
