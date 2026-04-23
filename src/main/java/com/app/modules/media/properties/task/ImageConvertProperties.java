package com.app.modules.media.properties.task;

import com.app.modules.media.enums.MediaSize;
import lombok.*;

import static com.app.modules.media.properties.MediaTypeProperties.IMAGE;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ImageConvertProperties implements MediaConvertProperties {
    @Builder.Default
    private MediaSize mediaSize = MediaSize.CUSTOM;

    @Builder.Default
    private String extension = IMAGE.targetExtension();

    @Builder.Default
    private String contentType = IMAGE.targetContentType();

    @Builder.Default
    private Integer width = 1920;

    @Builder.Default
    private Integer height = 1080;

    @Builder.Default
    private Boolean keepAspectRatio = true;

    public static ImageConvertProperties fromMediaSize(@NonNull MediaSize size) {
        return ImageConvertProperties.builder()
                .build()
                .toBuilder()
                .keepAspectRatio(size.isKeepAspectRatio())
                .mediaSize(size)
                .height(size.getHeight())
                .width(size.getWidth())
                .build();
    }
}
