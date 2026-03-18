package com.app.dto;


import lombok.Builder;

@Builder
public record AuthDTO(
        String email,
        String password
) {

}
