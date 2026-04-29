package com.app.modules.media.enums;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MediaSizeConverter implements Converter<String, MediaSize> {
    @Override
    public MediaSize convert(String source) {
        if (source == null) {
            return null;
        }

        return MediaSize.valueOf(source.toUpperCase().trim());
    }
}
