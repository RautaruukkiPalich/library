package com.app.modules.media.converter;

import com.app.modules.media.enums.MediaContent;

import java.io.IOException;
import java.io.InputStream;

public interface MediaConverter {
    InputStream convert(InputStream source, ConversionParams cp) throws IOException;

    MediaContent getSupportedType();
}
