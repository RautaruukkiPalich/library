package com.app.modules.media.converter.image;

import com.app.modules.media.converter.ConversionParams;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface ImageConverter {
    InputStream convert(@NonNull InputStream stream,
                        @NonNull ConversionParams cp) throws IOException;
}
