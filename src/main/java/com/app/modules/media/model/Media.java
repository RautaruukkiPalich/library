package com.app.modules.media.model;

import com.app.core.model.BaseModel;
import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaTaskValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "medias")
public class Media extends BaseModel {
    @Id
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private Boolean isPublic;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaContent mediaContent;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaFile> files = new ArrayList<>();

    @Transient
    public MediaFile getOriginal() {
        return files.stream()
                .filter(f -> f.getMediaSize() == MediaSize.ORIGINAL)
                .findFirst()
                .orElse(null);
    }

    @Transient
    public MediaFile getWithMediaSize(MediaSize mediaSize) {
        return files.stream()
                .filter(f -> f.getMediaSize() == mediaSize)
                .findFirst()
                .orElse(null);
    }

    public Media() {
        super(MediaTaskValidationException::new);
    }

    public static Media create(
            Long userId,
            String originalFilename,
            MediaContent content
    ) {
        return create(userId, originalFilename, false, content);
    }

    public static Media create(
            Long userId,
            String originalFilename,
            Boolean isPublic,
            MediaContent content
    ) {
        Media media = new Media();
        media.setUuid(UUID.randomUUID());
        media.setUserId(userId);
        media.setOriginalFilename(originalFilename);
        media.setIsPublic(isPublic);
        media.setMediaContent(content);
        return media;
    }
}
