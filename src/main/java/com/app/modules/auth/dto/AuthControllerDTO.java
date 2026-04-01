package com.app.modules.auth.dto;

import com.app.core.utils.apiValidations.EmailValidation;
import com.app.core.utils.apiValidations.PasswordValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Schema(description = "ControllerAuthDTO")
public class AuthControllerDTO {

    @Setter
    @Getter
    @SuperBuilder
    @NoArgsConstructor
    @Schema(name = "register user", description = "register user")
    public static class Register extends Login {
        @Schema(description = "user firstname", example = "Alexander", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("firstname")
        @NotBlank(message = "firstname is required")
        @Size(min = 2, max = 100, message = "firstname must be between 2 and 100 characters")
        private String firstname;

        @Schema(description = "user lastname", example = "Pushkin", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("lastname")
        @NotBlank(message = "lastname is required")
        @Size(min = 2, max = 100, message = "lastname must be between 2 and 100 characters")
        private String lastname;

        @Schema(description = "user surname", example = "Sergeevich", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("surname")
        @NotBlank(message = "surname is required")
        @Size(min = 2, max = 100, message = "surname must be between 2 and 100 characters")
        private String surname;
    }

    @Setter
    @Getter
    @SuperBuilder
    @NoArgsConstructor
    @Schema(name = "email")
    public static class Email {
        @JsonProperty("email")
        private String email;
    }

    @Setter
    @Getter
    @SuperBuilder
    @NoArgsConstructor
    @Schema(name = "login user", description = "login user")
    public static class Login implements EmailValidation, PasswordValidation {
        @JsonProperty("email")
        private String email;

        @JsonProperty("password")
        private String password;
    }

    @Setter
    @Getter
    @SuperBuilder
    @NoArgsConstructor
    @Schema(name = "refresh tokens", description = "refresh tokens")
    public static class RefreshTokens {

        @Schema(description = "refresh token", example = "dasdsad.dsa.dsa.d", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("refresh_token")
        @NotBlank(message = "refresh_token is required")
        private String token;
    }

    @Schema(name = "token pair response", description = "access + refresh token pair")
    public static class TokenPairResponse {

        @Schema(description = "access token", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("access")
        private String accessToken;

        @Schema(description = "refresh token", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("refresh")
        private String refreshToken;

        public TokenPairResponse(String access, String refresh) {
            this.accessToken = access;
            this.refreshToken = refresh;
        }
    }
}
