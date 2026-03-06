package com.app.utils.validator;

import java.time.OffsetDateTime;
import java.util.Map;

public class OffsetDateTimeValidator extends Validator<OffsetDateTime, OffsetDateTimeValidator>{

    private static final String ERROR_DATE_TOO_EARLY = "date is too early";
    private static final String ERROR_DATE_TOO_LATE = "date is too late";

    public OffsetDateTimeValidator(String key, OffsetDateTime value){
        super(key, value);
    }

    public OffsetDateTimeValidator after(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            addCheck(() ->
                    value.isBefore(offsetDateTime) ? Map.of(key, ERROR_DATE_TOO_EARLY) : null
            );
        }
        return this;
    }

    public OffsetDateTimeValidator before(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            addCheck(() ->
                    value.isAfter(offsetDateTime) ? Map.of(key, ERROR_DATE_TOO_LATE) : null
            );
        }
        return this;
    }
}
