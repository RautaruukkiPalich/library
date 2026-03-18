package com.app.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Schema(description = "ControllerUserDTO")
public class ControllerUserDTO {

    @Getter
    @Setter
    @SuperBuilder
    @Schema(name = "response user", description = "response for user")

    public static class Response{
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
    }
}
