package com.app.modules.media.properties.task;

import com.app.modules.media.enums.MediaSize;

public interface MediaConvertProperties {
    MediaSize getMediaSize();
    String getExtension();
    Integer getWidth();
    Integer getHeight();
    Boolean getKeepAspectRatio();
    String getContentType();
}
