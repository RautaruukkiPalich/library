package com.app.modules.auth.dto;


import lombok.Builder;

@Builder
public record LoginDTO(
        String email,
        String password
) {

}
