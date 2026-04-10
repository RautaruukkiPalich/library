package com.app.modules.media.converter;

import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.model.MediaFile;
import lombok.NonNull;

import java.io.IOException;

public interface MediaConverter {
    MediaFile convert(@NonNull MediaFile originalMediaFile,
                      @NonNull MediaSize size) throws IOException;
}
