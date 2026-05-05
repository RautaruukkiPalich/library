package com.app.modules.media.service;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.properties.DefaultMediaProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaSizeService {
    private final DefaultMediaProperties properties;

    public DefaultMediaProperties.SizeConfig getSizeConfig(
            @NonNull MediaContent content,
            @NonNull MediaSize size) {
        var typeConfig = properties.getTypes().get(content);
        if (typeConfig == null) {
            throw new IllegalArgumentException("unknown content type: %s".formatted(content));
        }

        var sizeConfig = typeConfig.getSizes().get(size);
        if (sizeConfig == null) {
            throw new IllegalArgumentException(
                    "size %s not configured for %s".formatted(size, content)
            );
        }

        return sizeConfig;
    }

    public Set<MediaSize> getAvailableSizes(@NonNull MediaContent content) {
        var typeConfig = properties.getTypes().get(content);
        if (typeConfig == null) {
            return Set.of();
        }
        return typeConfig.getSizes().keySet();
    }

    public Set<MediaSize> getDefaultConvertSizes(@NonNull MediaContent contentType) {
        return getAvailableSizes(contentType).stream()
                .filter(size -> size != MediaSize.ORIGINAL && size != MediaSize.CUSTOM)
                .collect(Collectors.toSet());
    }

    public boolean hasSize(MediaContent contentType, MediaSize size) {
        var typeConfig = properties.getTypes().get(contentType);
        if (typeConfig == null) return false;
        return typeConfig.getSizes().containsKey(size);
    }
}
