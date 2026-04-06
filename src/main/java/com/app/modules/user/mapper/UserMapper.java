package com.app.modules.user.mapper;

import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.dto.UserControllerDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.model.User;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserMapper {

    public static UserAuthInfoDTO convertToAuthInfo(@NonNull User user) {
        Objects.requireNonNull(user, "user must not be null");
        return UserAuthInfoDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .build();
    }

    public static UserDTO toDTO(@NonNull User user) {
        Objects.requireNonNull(user, "user must not be null");
        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserControllerDTO.Response.PublicProfile toPublicResponse(@NonNull UserDTO u) {
        Objects.requireNonNull(u, "user must not be null");
        return UserControllerDTO.Response.PublicProfile
                .builder()
                .id(u.id())
                .firstname(u.firstname())
                .surname(u.surname())
                .lastname(u.lastname())
                .email(u.email())
                .role(u.role())
                .build();
    }

    public static UserControllerDTO.Response.PrivateProfile toPrivateResponse(@NonNull UserDTO u) {
        Objects.requireNonNull(u, "user must not be null");
        return UserControllerDTO.Response.PrivateProfile
                .builder()
                .id(u.id())
                .firstname(u.firstname())
                .surname(u.surname())
                .lastname(u.lastname())
                .email(u.email())
                .role(u.role())
                .build();
    }
}
