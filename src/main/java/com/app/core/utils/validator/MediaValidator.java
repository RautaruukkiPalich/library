package com.app.core.utils.validator;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

public class MediaValidator<T extends MultipartFile> extends Validator<T, MediaValidator<T>> {
    public MediaValidator(String key, T value) {
        super(key, value);
    }

    public MediaValidator<T> maxSize(Long max) {
        if (max != null) {
            addCheck(() ->
                    value.getSize() > max ? Map.of(key, "size is too high") : null
            );
        }
        return this;
    }

    public MediaValidator<T> contentType(String[] allowedMediaTypes) {
        String contentType = value.getContentType();
        if (contentType == null) {
            addCheck(() -> Map.of(key, "null content type"));
            return this;
        }

        addCheck(() ->
                Arrays.stream(allowedMediaTypes).noneMatch(mt -> mt.contains(contentType)) ?
                        Map.of(key, "invalid content type. require one of: [%s]".formatted(Arrays.toString(allowedMediaTypes))) :
                        null
        );

        return this;
    }
}
