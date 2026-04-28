package com.app.modules.media.exceptions;

import com.app.modules.media.enums.TaskStatus;

public class TaskTransitionException extends RuntimeException {
    public TaskTransitionException(String message) {
        super(message);
    }

    public static TaskTransitionException invalidStatus(TaskStatus from, TaskStatus to) {
        return new TaskTransitionException("can not transition from %s to %s".formatted(from, to));
    }
}
