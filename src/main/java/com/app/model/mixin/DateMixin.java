package com.app.model.mixin;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public class DateMixin {

    private final static String DEFAULT = "TIMESTAMPTZ NOT NULL DEFAULT now()";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition =DEFAULT)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = DEFAULT)
    private OffsetDateTime updatedAt;

    protected DateMixin() {
    }
}
