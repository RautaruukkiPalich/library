package com.app.core.usecase;

import jakarta.validation.Valid;
import lombok.NonNull;

public interface BaseUseCase<I, O> {
    O execute(@Valid @NonNull I input);
}
