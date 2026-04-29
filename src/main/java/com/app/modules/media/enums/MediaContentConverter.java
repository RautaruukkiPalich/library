package com.app.modules.media.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MediaContentConverter implements Converter<String, MediaContent> {
    @Override
    public MediaContent convert(String source) {
        return MediaContent.fromName(source);
    }
}