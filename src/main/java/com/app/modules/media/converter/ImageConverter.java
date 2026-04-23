package com.app.modules.media.converter;

import com.app.modules.media.properties.task.MediaConvertProperties;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface ImageConverter {
    InputStream convert(@NonNull InputStream stream,
                        @NonNull MediaConvertProperties props) throws IOException;
}
