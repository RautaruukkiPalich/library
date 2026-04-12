package com.app.modules.media.utils;

import com.app.core.exception.ValidationException;
import com.app.core.utils.validator.ObjectValidator;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.source.MediaSource;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

import static com.app.modules.media.utils.FileOperations.extractExtension;

public class FileValidator {

    private final static Set<String> TRAVERSAL_CHARACTERS = Set.of("\\", "/", ":", "*", "?", "\"", "<", ">", "|");

    public static void baseValidation(@NonNull MediaSource mediaSource) throws ValidationException {
        Map<String, String> validateErrors = new ObjectValidator<>("file", mediaSource)
                .notNull()
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

    public static void validateImageFile(@NonNull MediaSource mediaSource,
                                         @NonNull MediaContent type) throws ValidationException {

        String contentType = mediaSource.getSanitizedContentType();

        Map<String, String> validateErrors = new ObjectValidator<>("file", mediaSource)
                .validateString((s) -> contentType, "content_type",
                        v -> v.in(type.getContentTypes(), "is not allowed"))
                .validateString(MediaSource::getOriginalFilename, "original_filename",
                        v -> v
                                .custom(s -> {
                                    String ext = extractExtension(s);
                                    if (!type.getExtensions().contains(ext)) {
                                        return false;
                                    }

                                    String expectedType = type.getExtensionMap().get(ext);
                                    if (expectedType == null) {
                                        return false;
                                    }
                                    return contentType.equalsIgnoreCase(expectedType);
                                }, "invalid extension or does not match content type"))
                .validateNumber(MediaSource::getSize, "size",
                        v -> v.min(type.getMinSize()).max(type.getMaxSize()))
                .validate();

        checkAndThrowValidateException(validateErrors);
    }


    private static void checkAndThrowValidateException(Map<String, String> errors) throws ValidationException {
        if (errors != null && !errors.isEmpty()) {
            throw new FileValidationException(errors);
        }
    }

}
