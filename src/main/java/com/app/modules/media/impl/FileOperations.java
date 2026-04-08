package com.app.modules.media.impl;

import com.app.core.exception.ValidationException;
import com.app.core.utils.validator.ObjectValidator;
import com.app.modules.media.exceptions.FileValidationException;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FileOperations {
    private final static String[] TRAVERSAL_CHARACTERS = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    private final static String[] IMAGE_EXTENSIONS = new String[]{"jpeg", "jpg", "png", "gif", "webp"};
    private final static String[] IMAGE_CONTENT_TYPES = new String[]{"image/jpeg", "image/png", "image/gif", "image/webp"};

    private final static Long MIN_FILE_SIZE = 1L;
    private final static Long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    private final static Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.of(
            "jpeg", "image/jpeg",
            "jpg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "webp", "image/webp"
    );

    public static void validateFile(@NonNull MultipartFile file) throws ValidationException {
        Map<String, String> validateErrors = new ObjectValidator<>("file", file)
                .notNull()
                .validateNumber(
                        MultipartFile::getSize,
                        "size",
                        v -> v.notNull()
                                .min(MIN_FILE_SIZE)
                                .max(MAX_FILE_SIZE)
                )
                .validateString(
                        MultipartFile::getContentType,
                        "content_type",
                        v -> v.notNull()
                                .notBlank()
                                .in(IMAGE_CONTENT_TYPES,
                                        "file type is not allowed")
                )
                .validateString(
                        MultipartFile::getOriginalFilename,
                        "original_filename",
                        v -> v.notNull()
                                .notBlank()
                                .contains(".", "file must contain extension")
                                .custom(f -> 1 == f.chars().filter(ch -> ch == '.').count()
                                        , "multiple extensions detected")
                                .notContainsAny(TRAVERSAL_CHARACTERS,
                                        "filename contains path traversal characters")
                                .custom(FileOperations::hasExtension, "file does not have extension")
                                .custom(s-> {
                                    String ext = extractExtension(s);
                                    return Arrays.asList(IMAGE_EXTENSIONS).contains(ext);
                                }, "invalid file extension")
                                .custom(s-> {
                                    String ext = extractExtension(s);
                                    return Objects.equals(EXTENSION_TO_CONTENT_TYPE.get(ext), file.getContentType().split(";")[0]);
                                }, "extension does not match contentType")
                )
                .validate();

        if (validateErrors != null && !validateErrors.isEmpty()) {
            throw new FileValidationException(validateErrors);
        }
    }

    public static String extractFilename(@NonNull String filename) {
        int lastDotIndex = lastDotIndex(filename);
        if (lastDotIndex <= 0) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    public static String extractExtension(@NonNull String filename) {
        int lastDotIndex = lastDotIndex(filename);
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    public static boolean hasExtension(@NonNull String filename) {
        int lastDotIndex = lastDotIndex(filename);
        return lastDotIndex > 0 && lastDotIndex < filename.length() - 1;
    }

    private static int lastDotIndex(@NonNull String filename) {
        return filename.lastIndexOf(".");
    }
}
