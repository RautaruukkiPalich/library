package com.app.modules.media.metadata;

import com.app.modules.media.enums.MediaSize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import static com.app.modules.media.enums.MediaSize.DEFAULT_MEDIA_SIZE;
import static com.app.modules.media.properties.MediaTypeProperties.VIDEO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoMetadataImpl implements VideoMetadata {
    private String extension = VIDEO.targetExtension();
    private String contentType = VIDEO.targetContentType();
    private MediaSize mediaSize = MediaSize.CUSTOM;
    private Integer width = 1920;
    private Integer height = 1080;
    private Boolean keepAspectRatio = true;
    private Boolean cropToSquare = false;

    private static VideoMetadataImpl createDefaultByMediaSize(
            @NonNull MediaSize mediaSize
    ) {
        return new VideoMetadataImpl(
                VIDEO.targetExtension(),
                VIDEO.targetContentType(),
                mediaSize,
                mediaSize.getWidth(),
                mediaSize.getHeight(),
                mediaSize.isKeepAspectRatio(),
                !mediaSize.isKeepAspectRatio()
        );
    }

    public static VideoMetadataImpl create(
            @NonNull MediaSize mediaSize) {
        if (!DEFAULT_MEDIA_SIZE.contains(mediaSize)) {
            log.error("non default media size. actual {}", mediaSize.getCode());
            return new VideoMetadataImpl();
        }
        return createDefaultByMediaSize(mediaSize);
    }

    public static VideoMetadataImpl create(
            @NonNull MediaSize mediaSize,
            String extension,
            Integer height,
            Integer width,
            Boolean keepAspectRatio
    ) {
        VideoMetadataImpl md = create(mediaSize);

        if (DEFAULT_MEDIA_SIZE.contains(mediaSize)) {
            return md;
        }

        if (extension != null && VIDEO.extensions().contains(extension)) {
            md.extension = extension;
            md.contentType = VIDEO.extensionToContentType().get(md.extension);
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
    public VideoMetadataImpl toMediaFileMetadata() {
        VideoMetadataImpl md = new VideoMetadataImpl();
        md.setContentType(this.getContentType());
        md.setExtension(this.getExtension());
        md.setHeight(this.getHeight());
        md.setWidth(this.getWidth());
        return md;
    }
}



