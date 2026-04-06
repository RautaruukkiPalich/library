package com.app.modules.email.api;

import com.app.modules.email.dto.EmailDTO;
import lombok.NonNull;

public interface EmailPreparerService {
    void prepare(@NonNull EmailDTO dto);
}
