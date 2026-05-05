package com.app.core.usecase;

import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseCommandUseCase<I, O> implements BaseUseCase<I, O> {
    @Transactional
    @Override
    public abstract O execute(@Valid @NonNull I input);
}
