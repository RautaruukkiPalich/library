package com.app.modules.media.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.annotation.Transient;

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

    Long getFileSize();

    @JsonIgnore
    @Transient
    String getSpecDesc();
}
