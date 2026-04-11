package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.UploadStatus;
import com.app.modules.media.exceptions.MediaTaskValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "media_tasks")
public class MediaTask extends BaseModel {
    @Id
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "media_uuid", nullable = false)
    private UUID mediaUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uuid", referencedColumnName = "uuid",
            insertable = false, updatable = false)
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status;

    public MediaTask() {
        super(MediaTaskValidationException::new);
    }

    public void setMedia(@NonNull Media media) {
        this.mediaUuid = media.getUuid();
        this.media = media;
    }

    public void setMediaUuid(UUID uuid) {
        this.mediaUuid = uuid;
        this.media = null;
    }
}
