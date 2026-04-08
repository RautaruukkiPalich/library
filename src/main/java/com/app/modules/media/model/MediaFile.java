package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uuid", referencedColumnName = "uuid", nullable = false)
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaSize mediaSize;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String path;

    public MediaFile() {
        super(MediaFileValidationException::new);
    }

    public MediaFile(UUID uuid, String filename, String extension, String contentType,
                     MediaSize mediaSize, Long fileSize, String path) {
        super(MediaFileValidationException::new);
        this.uuid = uuid;
        this.filename = filename;
        this.extension = extension;
        this.contentType = contentType;
        this.mediaSize = mediaSize;
        this.fileSize = fileSize;
        this.path = path;
    }
}
