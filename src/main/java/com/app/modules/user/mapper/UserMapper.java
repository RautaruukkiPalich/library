package com.app.modules.user.mapper;

import com.app.modules.user.dto.ControllerUserDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserMapper {

    public static UserDTO toDTO(User user) {
        Objects.requireNonNull(user, "user cant be null");
        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static ControllerUserDTO.Response toResponse(UserDTO u) {
        Objects.requireNonNull(u, "author cant be null");
        return ControllerUserDTO.Response
                .builder()
                .id(u.id())
                .firstname(u.firstname())
                .surname(u.surname())
                .lastname(u.lastname())
                .email(u.email())
                .build();
    }
}
