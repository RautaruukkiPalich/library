package com.app.modules.media.processor;

import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface MediaProcessor {
    ByteArrayInputStream resize(@NonNull InputStream stream,
                                @NonNull MediaSize size,
                                @NonNull String extension) throws IOException;

    void resize(@NonNull InputStream source,
                @NonNull OutputStream dst,
                @NonNull MediaSize targetSize,
                @NonNull String extension) throws IOException;

    MediaContentType getSupportedContentType();

    Set<String> getSupportedOutputFormats();

    boolean canProcess(String contentType, String extension);
}
