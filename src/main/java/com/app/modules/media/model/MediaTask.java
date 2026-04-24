package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.converter.MediaMetadataClassConverter;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.MediaTaskValidationException;
import com.app.modules.media.metadata.MediaMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Convert(converter = MediaMetadataClassConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private MediaMetadata mediaMetadata = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "fail_reason")
    private String failReason;

    @PrePersist
    protected void checkConvertProperties() throws RuntimeException {
        if (mediaMetadata == null) throw new RuntimeException("empty media metadata");
    }

    public MediaTask() {
        super(MediaTaskValidationException::new);
    }

    public MediaTask(UUID uuid,
                     Long userId,
                     UUID mediaUuid,
                     MediaMetadata mediaMetadata,
                     TaskStatus taskStatus) {
        super(MediaTaskValidationException::new);
        this.uuid = uuid;
        this.userId = userId;
        this.mediaMetadata = mediaMetadata;
        this.mediaUuid = mediaUuid;
        this.status = taskStatus;
    }

    public static MediaTask create(
            Long userId,
            UUID mediaUuid,
            MediaMetadata mediaMetadata
    ) {
        return new MediaTask(
                UUID.randomUUID(),
                userId,
                mediaUuid,
                mediaMetadata,
                TaskStatus.PENDING
        );
    }

    public void setStatusCompleted() {
        this.setStatus(TaskStatus.COMPLETED);
    }

    public void setStatusFailed(String cause) {
        this.setStatus(TaskStatus.FAILED);
        this.setFailReason(cause);
    }
}
