package com.app.modules.email.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailDTO(
        String to,
        List<String> cc,
        List<String> bcc,
        String subject,
        String body,
        String HTMLBody
) {
}
