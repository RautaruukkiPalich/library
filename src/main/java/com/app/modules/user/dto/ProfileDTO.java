package com.app.modules.user.dto;


public class ProfileDTO {
    public record EditPassword(
            Long initiatorId,
            Long subjectId,
            String oldPassword,
            String newPassword
    ) {
    }

    public record EditFirstname(
            Long initiatorId,
            Long subjectId,
            String firstname
    ) {
    }

    public record EditLastname(
            Long initiatorId,
            Long subjectId,
            String lastname
    ) {
    }

    public record EditSurname(
            Long initiatorId,
            Long subjectId,
            String surname
    ) {
    }

    public record EditEmail(
            Long initiatorId,
            Long subjectId,
            String email
    ) {
    }
}
