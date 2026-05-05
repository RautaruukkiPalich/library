package com.app.modules.media.source;

import java.io.IOException;
import java.io.InputStream;

public interface MediaSource {
    InputStream getInputStream() throws IOException;

    String getContentType();

    String getOriginalFilename();

    Long getSize();

    boolean isEmpty();

    default String getSanitizedContentType() {
        return sanitizeContentType(getContentType());
    }

    private String sanitizeContentType(String contentType) {
        return (contentType == null) ?
                null :
                contentType.split(";")[0].trim().toLowerCase();
    }
}