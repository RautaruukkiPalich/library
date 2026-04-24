package com.app.modules.media.metadata;

import com.app.modules.media.enums.MediaSize;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImageMetadataImpl.class, name = "image"),
})
public interface MediaMetadata {
    String getExtension();

    String getContentType();

    MediaSize getMediaSize();

    Integer getWidth();

    Integer getHeight();

    Boolean getKeepAspectRatio();

    Boolean getCropToSquare();

    MediaMetadata toMediaFileMetadata();
}
