package com.app.modules.media.converter;

import com.app.modules.media.converter.impl.ThumbnailsImageConverter;
import com.app.modules.media.enums.MediaContent;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@AllArgsConstructor
public class ImageMediaConverter implements MediaConverter {
    private final ThumbnailsImageConverter converter;

    @Override
    public InputStream convert(@NonNull InputStream source,
                               @NonNull ConversionParams cp) throws IOException {
        log.info("converting image: {}x{}, keepRatio={}",
                cp.getWidth(), cp.getHeight(), cp.getKeepAspectRatio());
        return converter.convert(source, cp);
    }

    @Override
    public MediaContent getSupportedType() {
        return MediaContent.IMAGE;
    }
}
