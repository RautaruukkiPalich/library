package com.app.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "ControllerAuthorDTO")
public class ControllerAuthorDTO {

    @Setter
    @Getter
    @SuperBuilder
    @Schema(name = "create author", description = "create author")
    public static class Create {
        @Schema(description = "author firstname", example = "Alexander", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("firstname")
        @NotBlank(message = "firstname is required")
        @Size(min = 2, max = 255, message = "firstname must be between 2 and 255 characters")
        private String firstname;

        @Schema(description = "author lastname", example = "Pushkin", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("lastname")
        @NotBlank(message = "lastname is required")
        @Size(min = 2, max = 255, message = "lastname must be between 2 and 255 characters")
        private String lastname;

        @Schema(description = "author surname", example = "Sergeevich", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("surname")
        @NotBlank(message = "surname is required")
        @Size(min = 2, max = 255, message = "surname must be between 2 and 255 characters")
        private String surname;

        public Create() {
        }
    }

    @Setter
    @Getter
    @SuperBuilder
    @Schema(name = "response author", description = "response for author (includes all fields + id + datemixin)")
    public static class Response extends Create {

        @Schema(description = "unique author identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        private Long id;

        @Schema(description = "author created_at", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @Schema(description = "author updated_at", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;

        public Response() {
        }

    }

    @Schema(name = "authors list response")
    public static class ListResponse {

        @Schema(description = "authors", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("authors")
        private List<ControllerAuthorDTO.Response> authors;

        public ListResponse(List<ControllerAuthorDTO.Response> authors) {
            this.authors = authors;
        }
    }
}
