package com.app.modules.media.converter.impl;

import com.app.modules.media.converter.ConversionParams;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ThumbnailsImageConverter {
    public InputStream convert(@NonNull InputStream source,
                               @NonNull ConversionParams cp) throws IOException {
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        Thumbnails.of(source)
                .outputFormat(cp.getTargetExtension())
                .size(cp.getWidth(), cp.getHeight())
                .keepAspectRatio(cp.shouldKeepAspectRatio())
                .toOutputStream(dst);
        return new ByteArrayInputStream(dst.toByteArray());
    }
}


