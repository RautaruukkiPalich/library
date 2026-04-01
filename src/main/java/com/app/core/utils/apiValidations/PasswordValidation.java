package com.app.core.utils.apiValidations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface PasswordValidation {
    @Schema(description = "password", example = "QWErty123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
    @JsonProperty("password")
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "must contain uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "must contain lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "must contain digit")
    String getPassword();
}
