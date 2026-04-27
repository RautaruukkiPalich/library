package com.app.modules.media.converter.media.image;

import com.app.modules.media.converter.media.ConversionParams;
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
                               @NonNull ConversionParams cp) throws IOException {
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        Thumbnails.of(source)
                .outputFormat(cp.getTargetExtension())
                .size(cp.getWidth(), cp.getHeight())
                .keepAspectRatio(cp.getKeepAspectRatio())
                .toOutputStream(dst);
        return new ByteArrayInputStream(dst.toByteArray());
    }
}


