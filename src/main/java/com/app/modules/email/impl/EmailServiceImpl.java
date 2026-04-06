package com.app.modules.email.impl;

import com.app.modules.email.api.EmailPreparerService;
import com.app.modules.email.api.EmailSenderService;
import com.app.modules.email.dto.EmailDTO;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@NoArgsConstructor
public class EmailServiceImpl implements EmailPreparerService, EmailSenderService {
    //TODO: implement
    @Override
    public void send(@NonNull EmailDTO dto) {
        log.info("Send email. To: {}, Subject: {}, Body: {}", dto.to(), dto.subject(), dto.body());
    }

    @Override
    public void prepare(@NonNull EmailDTO dto) {
        log.info("Prepared email: To: {}, Subject: {}, Body: {}", dto.to(), dto.subject(), dto.body());
    }
}
