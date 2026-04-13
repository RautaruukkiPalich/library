package com.app.core.usecase;

import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseCommandUseCase<I, O> implements BaseUseCase<I, O> {
    @Transactional
    @Override
    public abstract O execute(@NonNull I input);
}
