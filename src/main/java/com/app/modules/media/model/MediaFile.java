package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.converter.MediaMetadataClassConverter;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileValidationException;
import com.app.modules.media.metadata.MediaMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "media_files")
public class MediaFile extends BaseModel {
    @Id
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String contentType;

    @Column(name = "media_uuid", nullable = false)
    private UUID mediaUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaSize mediaSize;

    @Convert(converter = MediaMetadataClassConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private MediaMetadata metadata = null;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String path;

    public MediaFile() {
        super(MediaFileValidationException::new);
    }

    public MediaFile(UUID uuid, String filename, String extension, String contentType,
                     MediaSize mediaSize, @NonNull Media media, Long fileSize, String path, MediaMetadata metadata) {
        super(MediaFileValidationException::new);
        this.uuid = uuid;
        this.filename = filename;
        this.extension = extension;
        this.contentType = contentType;
        this.mediaSize = mediaSize;
        this.fileSize = fileSize;
        this.path = path;
        this.metadata = metadata == null ? null : metadata.toMediaFileMetadata();

        setMedia(media);
    }

    public static MediaFile create(
            String filename,
            @NonNull Media media,
            Long fileSize,
            String path,
            MediaMetadata metadata
    ) {
        return new MediaFile(
                UUID.randomUUID(),
                filename,
                metadata.getExtension(),
                metadata.getContentType(),
                metadata.getMediaSize(),
                media,
                fileSize,
                path,
                metadata.toMediaFileMetadata()
        );
    }

    public static MediaFile create(
            String filename,
            String extension,
            String contentType,
            MediaSize mediaSize,
            @NonNull Media media,
            Long fileSize,
            String path,
            MediaMetadata metadata
    ) {
        return new MediaFile(
                UUID.randomUUID(),
                filename,
                extension,
                contentType,
                mediaSize,
                media,
                fileSize,
                path,
                metadata.toMediaFileMetadata()
        );
    }

    public void setMedia(@NonNull Media m) {
        this.media = m;
        this.mediaUuid = m.getUuid();
    }

    public void setMediaUuid(@NonNull UUID mediaUuid) {
        this.mediaUuid = mediaUuid;
        this.media = null;
    }

    public UUID getMediaUuid() {
        if (mediaUuid == null && media != null) {
            return media.getUuid();
        }
        return mediaUuid;
    }

    public boolean compare(@NonNull MediaMetadata md) {
        if (md.getHeight() == null || md.getHeight() > this.metadata.getHeight()) {
            return false;
        }
        if (md.getWidth() == null || md.getWidth() > this.metadata.getWidth()) {
            return false;
        }
        return true;
    }
}
