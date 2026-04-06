package com.app.modules.email.api;

import com.app.modules.email.dto.EmailDTO;
import org.jspecify.annotations.NonNull;

public interface EmailSenderService {
    void send(@NonNull EmailDTO dto);
}
