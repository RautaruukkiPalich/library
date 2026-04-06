package com.app.modules.user.dto;


import org.jspecify.annotations.NonNull;

public class ProfileDTO {
    public record EditPassword(
            Long initiatorId,
            @NonNull Long subjectId,
            @NonNull String oldPassword,
            @NonNull String newPassword
    ) {
    }

    public record EditFirstname(
            Long initiatorId,
            @NonNull Long subjectId,
            @NonNull String firstname
    ) {
    }

    public record EditLastname(
            Long initiatorId,
            @NonNull Long subjectId,
            @NonNull String lastname
    ) {
    }

    public record EditSurname(
            Long initiatorId,
            @NonNull Long subjectId,
            @NonNull String surname
    ) {
    }

    public record EditEmail(
            Long initiatorId,
            @NonNull Long subjectId,
            @NonNull String email
    ) {
    }
}
