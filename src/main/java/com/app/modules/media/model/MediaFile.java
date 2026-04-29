package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileValidationException;
import com.app.modules.media.metadata.MediaMetadata;
import com.app.modules.media.metadata.MediaMetadataDatabaseConverter;
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
    private String contentType;

    @Column(name = "media_uuid", nullable = false)
    private UUID mediaUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaSize mediaSize;

    @Convert(converter = MediaMetadataDatabaseConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private MediaMetadata metadata = null;

    @Column(nullable = false)
    private String path;

    public MediaFile() {
        super(MediaFileValidationException::new);
    }

    private static MediaFile createPrivate(@NonNull UUID uuid,
                                           @NonNull String filename,
                                           @NonNull MediaSize mediaSize,
                                           @NonNull Media media,
                                           @NonNull String path,
                                           @NonNull String contentType,
                                           @NonNull MediaMetadata metadata) {
        MediaFile mf = new MediaFile();
        mf.uuid = uuid;
        mf.filename = filename;
        mf.path = path;
        mf.mediaSize = mediaSize;
        mf.contentType = contentType;
        mf.metadata = metadata;
        mf.setMedia(media);

        return mf;
    }

    public static MediaFile create(@NonNull String filename,
                                   @NonNull MediaSize mediaSize,
                                   @NonNull Media media,
                                   @NonNull String path,
                                   @NonNull String contentType,
                                   @NonNull MediaMetadata metadata
    ) {
        return createPrivate(
                UUID.randomUUID(),
                filename,
                mediaSize,
                media,
                path,
                contentType,
                metadata
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
        if (media != null) {
            return media.getUuid();
        }
        return mediaUuid;
    }
}
