package com.app.modules.user.dto;

import com.app.core.security.rbac.Role;
import com.app.core.utils.apiValidations.PasswordValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Schema(description = "ControllerUserDTO")
public class UserControllerDTO {

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "response user public")
    public static class PublicResponse {
        @Schema(description = "unique user identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        private Long id;

        @Schema(description = "user firstname", example = "Alexander", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("firstname")
        private String firstname;

        @Schema(description = "user lastname", example = "Pushkin", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("lastname")
        private String lastname;

        @Schema(description = "user surname", example = "Sergeevich", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("surname")
        private String surname;

        @Schema(description = "user email", example = "test@test.test", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("email")
        private String email;

        @Schema(description = "role", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("role")
        private Role role;
    }

    @Setter
    @SuperBuilder
    @Schema(name = "response user private")
    public static class PrivateResponse extends PublicResponse {}

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @Schema(name = "change password schema")
    public static class ChangePassword implements PasswordValidation {
        @JsonProperty("old_password")
        @Schema(description = "old_password", example = "QWErty123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @NotBlank(message = "old_password is required")
        @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
        @Pattern(regexp = ".*[A-Z].*", message = "must contain uppercase letter")
        @Pattern(regexp = ".*[a-z].*", message = "must contain lowercase letter")
        @Pattern(regexp = ".*\\d.*", message = "must contain digit")
        private String oldPassword;

        @JsonProperty("password")
        private String password;
    }
}
