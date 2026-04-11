package com.app.modules.media.converter;

import com.app.modules.media.dto.ConvertResultDTO;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.io.IOException;
import java.util.UUID;

public interface MediaConverter {
    ConvertResultDTO convert(@NonNull UUID mediaUuid,
                             @NonNull String pathToOriginal,
                             @NonNull MediaSize size,
                             @NonNull String targetExtension) throws IOException;
}
