package com.app.modules.media.validator.base;

import com.app.core.utils.validator.NumberValidator;
import com.app.core.utils.validator.ObjectValidator;
import com.app.core.utils.validator.StringValidator;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.properties.MediaTypeProperties;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.utils.FileOperations;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

import static com.app.modules.media.utils.FileOperations.extractExtension;

public abstract class BaseMediaValidator implements MediaValidator {
    private final static Set<String> TRAVERSAL_CHARACTERS = Set.of("\\", "/", ":", "*", "?", "\"", "<", ">", "|");

    @Override
    public final void validate(@NonNull MediaSource source) throws FileValidationException {
        baseValidation(source);
        validateContentType(source);
        validateSize(source);
        validateExtension(source);
        doValidate(source);
    }

    @Override
    public abstract MediaContent getSupportedType();

    protected abstract MediaTypeProperties getProperties();

    protected abstract void doValidate(@NonNull MediaSource source) throws FileValidationException;

    protected void baseValidation(@NonNull MediaSource mediaSource) throws FileValidationException {
        Map<String, String> validateErrors = new ObjectValidator<>("file", mediaSource)
                .notNull()
                .custom((s) -> !s.isEmpty(), "is empty")
                .validateNumber(MediaSource::getSize, "size",
                        v -> v.notNull().min(1L))
                .validateString(MediaSource::getContentType, "content_type",
                        v -> v.notNull().notBlank())
                .validateString(MediaSource::getOriginalFilename, "original_filename",
                        v -> v.notNull().notBlank()
                                .custom(FileOperations::hasExtension, "file does not have extension")
                                .custom(f -> 1 == f.chars().filter(ch -> ch == '.').count()
                                        , "multiple extensions detected")
                                .notContainsAny(TRAVERSAL_CHARACTERS,
                                        "filename contains path traversal characters"))
                .validate();

        checkAndThrowValidateException(validateErrors);
    }

    protected void validateContentType(@NonNull MediaSource source) throws FileValidationException {
        String contentType = source.getSanitizedContentType();

        checkAndThrowValidateException(
                new StringValidator("content_type", contentType)
                        .in(getProperties().contentTypes())
                        .validate()
        );
    }

    protected void validateSize(@NonNull MediaSource source) throws FileValidationException {
        checkAndThrowValidateException(
                new NumberValidator<>("size", source.getSize())
                        .min(getProperties().minSize())
                        .max(getProperties().maxSize())
                        .validate()
        );
    }

    protected void validateExtension(@NonNull MediaSource source) throws FileValidationException {
        String contentType = source.getSanitizedContentType();

        checkAndThrowValidateException(
                new StringValidator("original_filename", extractExtension(source.getOriginalFilename()))
                        .in(getProperties().extensions(), "invalid extension")
                        .custom((e) -> getProperties().extensionToContentType().get(e).equals(contentType),
                                "extension does not match content type")
                        .validate()
        );
    }

    protected static void checkAndThrowValidateException(Map<String, String> errors) throws FileValidationException {
        if (errors != null && !errors.isEmpty()) {
            throw new FileValidationException(errors);
        }
    }
}
