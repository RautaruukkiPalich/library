package com.app.modules.media.processor;

import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MediaProcessor {
    ConvertResultDTO resize(@NonNull InputStream stream,
                            @NonNull MediaSize targetSize,
                            @NonNull String extension) throws IOException;

    MediaContentType getSupportedContentType();

    List<String> getSupportedOutputFormats();

    boolean canProcess(String contentType, String extension);
}
