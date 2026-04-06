package com.app.modules.user.dto;

import com.app.core.security.rbac.Role;
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

    public static class Request {
        @Getter
        @Setter
        @SuperBuilder
        @NoArgsConstructor
        @Schema(name = "request edit password schema")
        public static class EditPassword {
            @JsonProperty("old_password")
            @Schema(description = "old_password", example = "QWErty123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
            @NotBlank(message = "is required")
            @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
            @Pattern(regexp = ".*[A-Z].*", message = "must contain uppercase letter")
            @Pattern(regexp = ".*[a-z].*", message = "must contain lowercase letter")
            @Pattern(regexp = ".*\\d.*", message = "must contain digit")
            private String oldPassword;

            @Schema(description = "password", example = "QWErty123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
            @JsonProperty("password")
            @NotBlank(message = "is required")
            @Size(min = 8, max = 100, message = "must be between 2 and 100 characters")
            @Pattern(regexp = ".*[A-Z].*", message = "must contain uppercase letter")
            @Pattern(regexp = ".*[a-z].*", message = "must contain lowercase letter")
            @Pattern(regexp = ".*\\d.*", message = "must contain digit")
            private String password;
        }

        @Getter
        @Setter
        @Schema(name = "request edit firstname schema")
        public static class EditFirstname {
            @Schema(description = "firstname", example = "Alexander", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
            @JsonProperty("firstname")
            @NotBlank(message = "is required")
            @Size(min = 2, max = 100, message = "must be between 2 and 100 characters")
            private String firstname;
        }

        @Getter
        @Setter
        @Schema(name = "request edit surname schema")
        public static class EditSurname {
            @Schema(description = "surname", example = "Sergeevich", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
            @JsonProperty("surname")
            @NotBlank(message = "is required")
            @Size(min = 2, max = 100, message = "must be between 2 and 100 characters")
            private String surname;
        }

        @Getter
        @Setter
        @Schema(name = "request edit lastname schema")
        public static class EditLastname {
            @Schema(description = "lastname", example = "Pushkin", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
            @JsonProperty("lastname")
            @NotBlank(message = "is required")
            @Size(min = 2, max = 100, message = "must be between 2 and 100 characters")
            private String lastname;
        }

        @Getter
        @Setter
        @Schema(name = "request edit firstname schema")
        public static class EditEmail {
            @Schema(description = "email", example = "test@test.test", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 100)
            @JsonProperty("email")
            @NotBlank(message = "is required")
            @Size(min = 5, max = 100, message = "must be between 5 and 100 characters")
            @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$", message = "invalid format. expect 'test@test.test'")
            private String email;
        }
    }

    public static class Response {
        @Getter
        @Setter
        @SuperBuilder
        @NoArgsConstructor
        @Schema(name = "response user public schema")
        public static class PublicProfile {
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
        @Schema(name = "response user private schema")
        public static class PrivateProfile extends PublicProfile {
        }

    }
}
