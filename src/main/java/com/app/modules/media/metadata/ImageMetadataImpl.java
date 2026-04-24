package com.app.modules.media.metadata;

import com.app.modules.media.enums.MediaSize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import static com.app.modules.media.enums.MediaSize.DEFAULT_MEDIA_SIZE;
import static com.app.modules.media.properties.MediaTypeProperties.IMAGE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageMetadataImpl implements ImageMetadata {
    private String extension = IMAGE.targetExtension();
    private String contentType = IMAGE.targetContentType();
    private MediaSize mediaSize = MediaSize.CUSTOM;
    private Integer width = 1920;
    private Integer height = 1080;
    private Boolean keepAspectRatio = true;
    private Boolean cropToSquare = false;

    private static ImageMetadataImpl createDefaultByMediaSize(
            @NonNull MediaSize mediaSize
    ) {
        return new ImageMetadataImpl(
                IMAGE.targetExtension(),
                IMAGE.targetContentType(),
                mediaSize,
                mediaSize.getWidth(),
                mediaSize.getHeight(),
                mediaSize.isKeepAspectRatio(),
                !mediaSize.isKeepAspectRatio()
        );
    }

    public static ImageMetadataImpl create(
            @NonNull MediaSize mediaSize) {
        if (!DEFAULT_MEDIA_SIZE.contains(mediaSize)) {
            log.error("non default media size. actual {}", mediaSize.getCode());
            return new ImageMetadataImpl();
        }
        return createDefaultByMediaSize(mediaSize);
    }

    public static ImageMetadataImpl create(
            @NonNull MediaSize mediaSize,
            String extension,
            Integer height,
            Integer width,
            Boolean keepAspectRatio
    ) {
        ImageMetadataImpl md = create(mediaSize);

        if (DEFAULT_MEDIA_SIZE.contains(mediaSize)) {
            return md;
        }

        if (extension != null && IMAGE.extensions().contains(extension)) {
            md.extension = extension;
            md.contentType = IMAGE.extensionToContentType().get(md.extension);
        }

        if (width != null && width >= 1 && width <= 1920) {
            md.width = width;
        }

        if (height != null && height >= 1 && height <= 1080) {
            md.height = height;
        }

        if (keepAspectRatio != null) {
            md.keepAspectRatio = keepAspectRatio;
        }

        md.cropToSquare = !md.keepAspectRatio;

        return md;
    }

    @Override
    public ImageMetadataImpl toMediaFileMetadata() {
        ImageMetadataImpl md = new ImageMetadataImpl();
        md.setContentType(this.getContentType());
        md.setExtension(this.getExtension());
        md.setHeight(this.getHeight());
        md.setWidth(this.getWidth());
        return md;
    }
}
