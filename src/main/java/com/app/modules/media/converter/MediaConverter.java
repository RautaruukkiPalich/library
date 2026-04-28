package com.app.modules.media.converter;

import com.app.modules.media.enums.MediaContent;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface MediaConverter {
    InputStream convert(@NonNull InputStream source,
                        @NonNull ConversionParams cp) throws IOException;

    MediaContent getSupportedType();
}
