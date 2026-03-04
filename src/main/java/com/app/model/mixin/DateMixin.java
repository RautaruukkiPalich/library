package com.app.model.mixin;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public class DateMixin {
    //    private final static String DEFAULT = "TIMESTAMPTZ NOT NULL DEFAULT now()";
    @Value("${app.db.timestamp.definition:TIMESTAMPTZ NOT NULL DEFAULT now()}")
    private String timestampDefinition;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "#{@dateMixin.timestampDefinition}")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "#{@dateMixin.timestampDefinition}")
    private OffsetDateTime updatedAt;

    protected DateMixin() {
    }
}
