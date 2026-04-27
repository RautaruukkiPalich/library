package com.app.modules.media.converter;

import com.app.modules.media.converter.image.ImageConverter;
import com.app.modules.media.enums.MediaContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@Slf4j
@Component
public class ImageMediaConverter implements MediaConverter {
    private final ImageConverter converter;

    @Override
    public InputStream convert(InputStream source, ConversionParams cp) throws IOException {
        log.info("converting image: {}x{}, keepRatio={}",
                cp.getWidth(), cp.getHeight(), cp.getKeepAspectRatio());
        return converter.convert(source, cp);
    }

    @Override
    public MediaContent getSupportedType() {
        return MediaContent.IMAGE;
    }
}
