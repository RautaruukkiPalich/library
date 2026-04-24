package com.app.modules.media.converter;

import com.app.modules.media.metadata.ImageMetadata;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@AllArgsConstructor
public class ThumbnailsImageConverter implements ImageConverter {
    @Override
    public InputStream convert(@NonNull InputStream source,
                               @NonNull ImageMetadata metadata) throws IOException {
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        Thumbnails.of(source)
                .outputFormat(metadata.getExtension())
                .size(metadata.getWidth(), metadata.getHeight())
                .keepAspectRatio(metadata.getKeepAspectRatio())
                .toOutputStream(dst);
        return new ByteArrayInputStream(dst.toByteArray());
    }
}


