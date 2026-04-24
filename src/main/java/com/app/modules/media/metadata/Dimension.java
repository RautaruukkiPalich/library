package com.app.modules.media.metadata;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record Dimension(
        Integer width,
        Integer height
) {
    @Override
    @NonNull
    public String toString() {
        return "width=%s height=%s".formatted(width(), height());
    }
}
