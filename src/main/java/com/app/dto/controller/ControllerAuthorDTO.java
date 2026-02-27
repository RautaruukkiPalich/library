package com.app.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "dto")
public class ControllerAuthorDTO {

    @Schema(name = "create author", description = "create author")
    public static class Create{
        @Schema(description = "author firstname", example = "Alexander", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("firstname")
        @NotBlank(message = "firstname is required")
        private String firstname;

        @Schema(description = "author lastname", example = "Pushkin", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("lastname")
        @NotBlank(message = "lastname is required")
        private String lastname;

        @Schema(description = "author surname", example = "Sergeevich", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
        @JsonProperty("surname")
        @NotBlank(message = "surname is required")
        private String surname;


        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
    }

    @Schema(name = "response author", description = "response for author (includes all fields + id)")
    public static class Response extends Create{

        @Schema(description = "unique author identifier", example = "101", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Schema(name = "authors list response")
    public static class ListResponse {

        @Schema(description = "List of authors", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("authors")
        private List<ControllerAuthorDTO.Response> authors;

        public ListResponse(List<ControllerAuthorDTO.Response> authors) {
            this.authors = authors;
        }
    }
}
