package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.UploadStatusType;
import com.app.modules.media.exceptions.MediaTaskValidationException;
import jakarta.persistence.*;
import lombok.Getter;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uuid", referencedColumnName = "uuid")
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatusType status;

    public MediaTask() {
        super(MediaTaskValidationException::new);
    }
}
