package com.app.modules.auth.mapper;

import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.user.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthMapper {

    public static UserDTO toUserDTO(RegisterDTO dto) {
        Objects.requireNonNull(dto, "dto cant be null");
        return UserDTO.builder()
                .firstname(dto.firstname())
                .lastname(dto.lastname())
                .surname(dto.surname())
                .email(dto.email())
                .rawPassword(dto.rawPassword())
                .build();
    }
}
