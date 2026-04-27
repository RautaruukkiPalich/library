package com.app.modules.media.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import static com.app.modules.media.properties.MediaTypeProperties.VIDEO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoMetadataImpl implements VideoMetadata {
    private String extension = VIDEO.targetExtension();
    private Integer width;
    private Integer height;
    @JsonProperty("file_size")
    private Long fileSize;

    public static VideoMetadataImpl create(
            @NonNull String extension,
            @NonNull Integer height,
            @NonNull Integer width,
            @NonNull Long fileSize
    ) {
        return new VideoMetadataImpl(
                extension, width, height, fileSize
        );
    }

    @Override
    public String getSpecDesc() {
        return "";
    }
}



