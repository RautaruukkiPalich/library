package com.app.modules.media.validator.base;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.source.MediaSource;
import lombok.NonNull;

public interface MediaValidator {
    MediaContent getSupportedType();

    void validate(@NonNull MediaSource source) throws FileValidationException;
}
