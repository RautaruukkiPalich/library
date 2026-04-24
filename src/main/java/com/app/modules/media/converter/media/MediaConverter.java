package com.app.modules.media.converter.media;

import com.app.modules.media.metadata.MediaMetadata;

import java.io.IOException;
import java.io.InputStream;

public interface MediaConverter<T extends MediaMetadata> {
    InputStream convert(InputStream source, T metadata) throws IOException;

    Class<T> getSupportedType();
}
