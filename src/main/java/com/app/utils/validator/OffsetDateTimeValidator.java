package com.app.utils.validator;

import java.time.OffsetDateTime;
import java.util.Map;

public class OffsetDateTimeValidator extends Validator<OffsetDateTime, OffsetDateTimeValidator>{

    private static final String ERROR_DATE_TOO_EARLY = "date is too early";
    private static final String ERROR_DATE_TOO_LATE = "date is too late";

    public OffsetDateTimeValidator(String key, OffsetDateTime value){
        super(key, value);
    }

    public OffsetDateTimeValidator from(OffsetDateTime from) {
        if (from != null) {
            addCheck(() ->
                    value.isBefore(from) ? Map.of(key, ERROR_DATE_TOO_EARLY) : null
            );
        }
        return this;
    }

    public OffsetDateTimeValidator to(OffsetDateTime to) {
        if (to != null) {
            addCheck(() ->
                    value.isAfter(to) ? Map.of(key, ERROR_DATE_TOO_LATE) : null
            );
        }
        return this;
    }
}
