package com.app.core.usecase;

import lombok.NonNull;

public interface BaseUseCase <I, O>{
    O execute(@NonNull I input);
}
