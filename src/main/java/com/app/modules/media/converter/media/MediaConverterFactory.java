package com.app.modules.media.converter.media;

import com.app.modules.media.metadata.MediaMetadata;
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

    private final List<MediaConverter<?>> converters;
    private final Map<Class<?>, MediaConverter<?>> converterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (MediaConverter<?> converter : converters) {
            converterMap.put(converter.getSupportedType(), converter);
            log.info("registered converter for type: {}", converter.getSupportedType().getSimpleName());
        }

        log.info("registered types: {}", converterMap.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T extends MediaMetadata> InputStream convert(InputStream source, T metadata) throws IOException {
        Class<?> actualType = metadata.getClass();
        MediaConverter<T> converter = (MediaConverter<T>) converterMap.get(actualType);

        if (converter == null) {
            log.error("no converter found for type: {}", actualType);
            log.error("available converters: {}", converterMap.keySet());
            throw new IllegalArgumentException(
                    String.format("no converter found for metadata type: %s",
                            metadata.getClass().getSimpleName())
            );
        }

        log.debug("using converter for type: {}", metadata.getClass().getSimpleName());
        return converter.convert(source, metadata);
    }

    public boolean supports(MediaMetadata metadata) {
        return converterMap.containsKey(metadata.getClass());
    }
}
