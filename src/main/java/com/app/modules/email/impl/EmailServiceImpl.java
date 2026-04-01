package com.app.modules.email.impl;

import com.app.modules.email.api.EmailService;
import com.app.modules.email.dto.EmailDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@NoArgsConstructor
public class EmailServiceImpl implements EmailService {
    //TODO: implement
    @Override
    public void send(EmailDTO dto) {
        log.info("To: {}, Subject: {}, Body: {}", dto.to(), dto.subject(), dto.body());
    }
}
