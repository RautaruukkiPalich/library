package com.app.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "ControllerGenreDTO")
public class ControllerGenreDTO {

    @Setter
    @Getter
    @SuperBuilder
    @Schema(name = "create genre", description = "create genre")
    public static class Create {
        @Schema(description = "genre name", example = "lyric", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("name")
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters")
        private String name;

    }

    @Setter
    @Getter
    @SuperBuilder
    @Schema(name = "response genre", description = "response for genre (includes all fields + id)")
    public static class Response extends Create {

        @Schema(description = "unique genre identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        private Long id;

    }

    @Schema(name = "genre list response")
    public static class ListResponse {

        @Schema(description = "genres", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("genres")
        private List<Response> genres;

        public ListResponse(List<ControllerGenreDTO.Response> genres) {
            this.genres = genres;
        }
    }
}