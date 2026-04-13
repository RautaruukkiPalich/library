package com.app.modules.media.validator.image;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.properties.MediaTypeProperties;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.validator.base.BaseMediaValidator;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ImageMediaValidator extends BaseMediaValidator {
    @Override
    public MediaContent getSupportedType() {
        return MediaContent.IMAGE;
    }

    @Override
    protected MediaTypeProperties getProperties() {
        return getSupportedType().getProps();
    }

    @Override
    protected void doValidate(@NonNull MediaSource source) throws FileValidationException {
    }

}
