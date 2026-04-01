package com.app.core.utils.apiValidations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface EmailValidation {
    @Schema(description = "email", example = "test@test.test", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
    @JsonProperty("email")
    @NotBlank(message = "email is required")
    @Size(min = 2, max = 100, message = "email must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$")
    String getEmail();
}
