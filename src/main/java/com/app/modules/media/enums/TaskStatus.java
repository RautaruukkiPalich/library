package com.app.modules.media.enums;

import com.app.core.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum TaskStatus implements BaseEnum {
    PENDING, PROCESSING, CANCELLED, FAILED, COMPLETED;

    @JsonCreator
    public static TaskStatus fromName(String name) {
        return BaseEnum.fromName(TaskStatus.class, name);
    }

    public static TaskStatus fromNameOrThrow(String name) {
        return BaseEnum.fromNameOrThrow(TaskStatus.class, name);
    }

    @Override
    public String toString() {
        return BaseEnum.normalize(this.name());
    }

    private static final EnumSet<TaskStatus> FINAL_STATUSES = EnumSet.of(COMPLETED, FAILED, CANCELLED);
    private static final EnumSet<TaskStatus> ERROR_STATUSES = EnumSet.of(FAILED);
    private static final EnumSet<TaskStatus> ACTIVE_STATUSES = EnumSet.of(PENDING, PROCESSING);


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
}
