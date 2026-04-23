package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.exceptions.MediaTaskValidationException;
import com.app.modules.media.properties.task.ImageConvertProperties;
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

    // TODO: hardcode class
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "convert_properties", columnDefinition = "jsonb")
    private ImageConvertProperties convertProperties;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "fail_reason")
    private String failReason;

    @PrePersist
    protected void checkConvertProperties() throws RuntimeException {
        if (convertProperties == null) throw new RuntimeException("empty convert properties");
    }

    public MediaTask() {
        super(MediaTaskValidationException::new);
    }

    public MediaTask(UUID uuid,
                     Long userId,
                     UUID mediaUuid,
                     ImageConvertProperties convertProperties,
                     TaskStatus taskStatus) {
        super(MediaTaskValidationException::new);
        this.uuid = uuid;
        this.userId = userId;
        this.convertProperties = convertProperties;
        this.mediaUuid = mediaUuid;
        this.status = taskStatus;
    }

    public static MediaTask create(
            Long userId,
            UUID mediaUuid,
            ImageConvertProperties convertProperties
    ) {
        return new MediaTask(
                UUID.randomUUID(),
                userId,
                mediaUuid,
                convertProperties,
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
