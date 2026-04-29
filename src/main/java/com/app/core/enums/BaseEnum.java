package com.app.core.enums;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Optional;

public interface BaseEnum {
    static String normalize(String name) {
        return name == null ? null : name.toUpperCase().trim();
    }

    static <T extends Enum<T> & BaseEnum> Optional<T> optionalFromName(@NonNull Class<T> cls, String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String normalized = normalize(name);

        return Arrays.stream(cls.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(normalized))
                .findFirst();
    }

    static <T extends Enum<T> & BaseEnum> T fromName(@NonNull Class<T> cls, String name) {
        return optionalFromName(cls, name).orElse(null);
    }

    static <T extends Enum<T> & BaseEnum> T fromNameOrThrow(@NonNull Class<T> cls, String name) {
        return optionalFromName(cls, name)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("illegal name '%s' for enum %s", name, cls.getSimpleName())
                ));
    }
}