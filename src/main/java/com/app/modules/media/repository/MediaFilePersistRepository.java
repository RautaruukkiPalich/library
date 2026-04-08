package com.app.modules.media.repository;

import com.app.modules.media.model.MediaFile;
import lombok.NonNull;

public interface MediaFilePersistRepository {
    MediaFile save(@NonNull MediaFile mediaFile);
}
