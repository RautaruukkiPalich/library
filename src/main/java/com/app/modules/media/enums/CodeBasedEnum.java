package com.app.modules.media.enums;

import java.util.Arrays;

public interface CodeBasedEnum {
    String getCode();

    default String getPreparedCode() {
        return preparedCode(getCode());
    }

    static String preparedCode(String code) {
        return code == null ? "" : code.toLowerCase().strip();
    }

    static <T extends Enum<T> & CodeBasedEnum> T fromCode(Class<T> enumClass, String code) throws IllegalArgumentException {
        String prepared = preparedCode(code);
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getPreparedCode().equals(prepared))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "no enum constant with code " + code));
    }
}
