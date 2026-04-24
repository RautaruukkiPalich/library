package com.app.modules.media.converter.media;

import com.app.modules.media.converter.ImageConverter;
import com.app.modules.media.metadata.ImageMetadataImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@Slf4j
@Component
public class ImageMediaConverter implements MediaConverter<ImageMetadataImpl> {
    private final ImageConverter converter;

    @Override
    public InputStream convert(InputStream source, ImageMetadataImpl metadata) throws IOException {
        log.info("converting image: {}x{}, keepRatio={}",
                metadata.getWidth(), metadata.getHeight(), metadata.getKeepAspectRatio());
        return converter.convert(source, metadata);
    }

    @Override
    public Class<ImageMetadataImpl> getSupportedType() {
        return ImageMetadataImpl.class;
    }
}
