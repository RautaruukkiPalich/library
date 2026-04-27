package com.app.modules.media.converter;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
