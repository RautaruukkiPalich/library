package com.app.modules.email.api;

import com.app.modules.email.dto.EmailDTO;

public interface EmailPreparerService {
    void prepare(EmailDTO dto);
}
