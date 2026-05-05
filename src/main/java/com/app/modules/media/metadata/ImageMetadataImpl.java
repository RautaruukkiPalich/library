package com.app.modules.media.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;

import static com.app.modules.media.properties.MediaTypeProperties.IMAGE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageMetadataImpl implements ImageMetadata {
    private String extension = IMAGE.targetExtension();
    private Integer width;
    private Integer height;
    @JsonProperty("file_size")
    private Long fileSize;

    public static ImageMetadataImpl create(
            @NonNull String extension,
            @NonNull Integer height,
            @NonNull Integer width,
            @NonNull Long fileSize
    ) {
        return new ImageMetadataImpl(
                extension, width, height, fileSize
        );
    }

    //TODO: fix null file size in database MediaFile
    @Override
    public Long getFileSize() {
        if (fileSize == null) return 0L;
        return fileSize;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getSpecDesc() {
        return "%s_%s".formatted(getWidth(), getHeight());
    }
}
