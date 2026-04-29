package com.app.modules.media.converter;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.properties.MediaSizeProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionParams {
    private MediaContent targetType;
    @Builder.Default
    private MediaSize targetSize = MediaSize.CUSTOM;
    private String targetExtension;
    @Builder.Default
    private Integer quality = 100;

    //image
    private Integer height;
    private Integer width;
    private Boolean keepAspectRatio;
    private Boolean cropToSquare;

    public boolean isImage() {
        return targetType == MediaContent.IMAGE;
    }

    public boolean isVideo() {
        return targetType == MediaContent.VIDEO;
    }

    public boolean shouldKeepAspectRatio() {
        return keepAspectRatio != null && keepAspectRatio;
    }

    public boolean shouldCropToSquare() {
        return cropToSquare != null && cropToSquare;
    }

    public static ConversionParams fromConfig(@NonNull MediaContent type,
                                              @NonNull MediaSize size,
                                              @NonNull String targetExtension,
                                              @NonNull MediaSizeProperties.SizeConfig config) {
        return ConversionParams.builder()
                .targetType(type)
                .targetSize(size)
                .targetExtension(targetExtension)
                .quality(config.getQuality())
                .width(config.getWidth())
                .height(config.getHeight())
                .keepAspectRatio(config.isKeepAspectRatio())
                .cropToSquare(config.isCropToSquare())
                .build();
    }
}
