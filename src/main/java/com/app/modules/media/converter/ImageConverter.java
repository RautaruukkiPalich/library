package com.app.modules.media.converter;

import com.app.modules.media.metadata.ImageMetadata;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface ImageConverter {
    InputStream convert(@NonNull InputStream stream,
                        @NonNull ImageMetadata metadata) throws IOException;
}
