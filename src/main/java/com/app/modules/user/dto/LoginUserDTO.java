package com.app.modules.user.dto;

import lombok.Builder;

@Builder
public record LoginUserDTO (
    String email,
    String password
){}
