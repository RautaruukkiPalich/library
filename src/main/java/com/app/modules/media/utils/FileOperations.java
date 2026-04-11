package com.app.modules.media.utils;

import lombok.NonNull;

public class FileOperations {
    public static String extractFilename(String filename) {
        if (filename == null) {
            return "";
        }
        if (!hasExtension(filename)) {
            return filename;
        }

        int lastDotIndex = lastDotIndex(filename);
        return filename.substring(0, lastDotIndex);
    }

    public static String extractExtension(String filename) {
        if (filename == null || !hasExtension(filename)) {
            return "";
        }

        int lastDotIndex = lastDotIndex(filename);
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
