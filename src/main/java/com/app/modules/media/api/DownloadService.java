package com.app.modules.media.api;

import com.app.modules.media.dto.DownloadedMediaDTO;
import com.app.modules.media.enums.MediaSize;
import lombok.NonNull;

import java.util.UUID;

public interface DownloadService {
    DownloadedMediaDTO download(@NonNull UUID uuid, @NonNull Long requesterId, @NonNull MediaSize size);
}
