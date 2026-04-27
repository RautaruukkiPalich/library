package com.app.modules.media.converter.media;

import com.app.modules.media.enums.MediaContent;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class MediaConverterFactory {

    private final List<MediaConverter> converters;
    private final Map<MediaContent, MediaConverter> converterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (MediaConverter converter : converters) {
            converterMap.put(converter.getSupportedType(), converter);
            log.info("registered converter for content: {}", converter.getSupportedType());
        }

        log.info("registered types: {}", converterMap.keySet());
    }

    public InputStream convert(InputStream source, ConversionParams cp) throws IOException {
        MediaConverter converter = converterMap.get(cp.getTargetType());

        if (converter == null) {
            log.error("no converter found for type: {}", cp.getTargetType());
            log.error("available converters: {}", converterMap.keySet());
            throw new IllegalArgumentException(
                    String.format("no converter found for metadata type: %s",
                            cp.getTargetType())
            );
        }

        log.debug("using converter for type: {}", cp.getTargetType());
        return converter.convert(source, cp);
    }

    public boolean supports(MediaContent content) {
        return converterMap.containsKey(content);
    }
}
