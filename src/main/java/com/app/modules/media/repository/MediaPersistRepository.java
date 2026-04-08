package com.app.modules.media.repository;

import com.app.modules.media.model.Media;
import lombok.NonNull;


public interface MediaPersistRepository {
    Media save(@NonNull Media media);
}
