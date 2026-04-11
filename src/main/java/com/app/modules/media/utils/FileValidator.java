package com.app.modules.media.utils;

import com.app.core.exception.ValidationException;
import com.app.core.utils.validator.ObjectValidator;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.exceptions.FileValidationException;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

import static com.app.modules.media.utils.FileOperations.extractExtension;

public class FileValidator {

    private final static Set<String> TRAVERSAL_CHARACTERS = Set.of("\\", "/", ":", "*", "?", "\"", "<", ">", "|");

    public static void baseValidation(@NonNull MultipartFile file) throws ValidationException {
        Map<String, String> validateErrors = new ObjectValidator<>("file", file)
                .notNull()
                .validateNumber(
                        MultipartFile::getSize,
                        "size",
                        v -> v.notNull().min(1L))
                .validateString(
                        MultipartFile::getContentType,
                        "content_type",
                        v -> v.notNull().notBlank())
                .validateString(
                        MultipartFile::getOriginalFilename,
                        "original_filename",
                        v -> v.notNull().notBlank()
                                .contains(".", "file must contain extension")
                                .custom(f -> 1 == f.chars().filter(ch -> ch == '.').count()
                                        , "multiple extensions detected")
                                .notContainsAny(TRAVERSAL_CHARACTERS,
                                        "filename contains path traversal characters")
                                .custom(FileOperations::hasExtension, "file does not have extension"))
                .validate();

        checkAndThrowValidateException(validateErrors);
    }

    public static void validateImageFile(@NonNull MultipartFile file,
                                         @NonNull MediaContentType type) throws ValidationException {
        Map<String, String> validateErrors = new ObjectValidator<>("file", file)
                .validateString(
                        MultipartFile::getOriginalFilename,
                        "original_filename",
                        v -> v
                                .custom(s -> {
                                    String ext = extractExtension(s);
                                    return type.getExtensions().contains(ext);
                                }, "invalid file extension")
                                .custom(s -> {
                                    String ext = extractExtension(s);
                                    String expectedType = type.getExtensionMap().getOrDefault(ext, "");
                                    String actualType = file.getContentType();
                                    return actualType != null && actualType.toLowerCase().startsWith(expectedType);
                                }, "extension does not match content type"))
                .validateNumber(
                        MultipartFile::getSize,
                        "size",
                        v -> v.min(type.getMinSize()).max(type.getMaxSize()))
                .validateString(
                        MultipartFile::getContentType,
                        "content_type",
                        v -> v.in(type.getContentTypes(), "file type is not allowed"))
                .validate();

        checkAndThrowValidateException(validateErrors);
    }


    private static void checkAndThrowValidateException(Map<String, String> errors) throws ValidationException {
        if (errors != null && !errors.isEmpty()) {
            throw new FileValidationException(errors);
        }
    }

}
