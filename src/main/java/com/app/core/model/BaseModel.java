package com.app.core.model;

import com.app.core.exception.ValidationException;
import com.app.core.utils.MapMerger;
import com.app.core.utils.validator.OffsetDateTimeValidator;
import com.app.core.utils.validator.ValidationExceptionFactory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PostPersist
    protected void postPersist(){
        validateStrict();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Transient
    private final ValidationExceptionFactory exceptionFactory;

    protected BaseModel(@NonNull ValidationExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    private static final String CREATED_AT_KEY = "createdAt";
    private static final String UPDATED_AT_KEY = "updatedAt";

    public void validate() throws ValidationException {
        checkErrorsAndThrow(
                collectValidateErrors(
                        validateBaseFields()
                )
        );
    }

    public void validateStrict() throws ValidationException {
        checkErrorsAndThrow(
                collectValidateErrors(
                        validateStrictFields()
                )
        );
    }

    private static final BinaryOperator<String> DEFAULT_MERGE_FUNC = (v1, v2) -> v1 + "; " + v2;

    protected final Map<String, String> collectValidateErrors(List<Map<String, String>> maps) {
        return new MapMerger<String, String>()
                .withMergeFunc(DEFAULT_MERGE_FUNC)
                .merge(maps);
    }

    protected List<Map<String, String>> validateBaseFields() {
        List<Map<String, String>> list = new ArrayList<>();
        addBaseValidations(list);
        return list;
    }

    protected List<Map<String, String>> validateStrictFields() {
        List<Map<String, String>> list = new ArrayList<>();
        addBaseValidations(list);
        addStrictValidations(list);
        return list;
    }

    private void addBaseValidations(List<Map<String, String>> list) {
    }

    private void addStrictValidations(List<Map<String, String>> list) {
        list.add(this.validateCreatedAt());
        list.add(this.validateUpdatedAt());
    }

    protected Map<String, String> validateCreatedAt() {
        return new OffsetDateTimeValidator(CREATED_AT_KEY, this.createdAt)
                .notNull()
                .before(OffsetDateTime.now())
                .validate();
    }

    protected Map<String, String> validateUpdatedAt() {
        return new OffsetDateTimeValidator(UPDATED_AT_KEY, this.updatedAt)
                .notNull()
                .before(OffsetDateTime.now())
                .validate();
    }

    protected final void checkErrorsAndThrow(Map<String, String> errors) throws ValidationException {
        if (errors != null && !errors.isEmpty()) {
            throw exceptionFactory.create(errors);
        }
    }
}