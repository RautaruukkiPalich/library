package com.app.modules.media.enums;

import com.app.core.enums.BaseEnum;

public enum SortOrder implements BaseEnum {
    ASC, DESC;

    public static SortOrder fromName(String name) {
        return BaseEnum.fromName(SortOrder.class, name);
    }

    public static SortOrder fromNameOrThrow(String name) {
        return BaseEnum.fromNameOrThrow(SortOrder.class, name);
    }

    public boolean isDesc() {
        return this == SortOrder.DESC;
    }


    public boolean isAsc() {
        return this == SortOrder.ASC;
    }


    @Override
    public String toString() {
        return BaseEnum.normalize(this.name());
    }
}
