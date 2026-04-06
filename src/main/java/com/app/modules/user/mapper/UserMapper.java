package com.app.modules.user.mapper;

import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.dto.UserControllerDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.model.User;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserAuthInfoDTO convertToAuthInfo(@NonNull User user) {
        return UserAuthInfoDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .build();
    }

    public static UserDTO toDTO(@NonNull User user) {
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

    public static UserControllerDTO.Response.PublicProfile toPublicResponse(@NonNull UserDTO dto) {
        return UserControllerDTO.Response.PublicProfile
                .builder()
                .id(dto.id())
                .firstname(dto.firstname())
                .surname(dto.surname())
                .lastname(dto.lastname())
                .email(dto.email())
                .role(dto.role())
                .build();
    }

    public static UserControllerDTO.Response.PrivateProfile toPrivateResponse(@NonNull UserDTO dto) {
        return UserControllerDTO.Response.PrivateProfile
                .builder()
                .id(dto.id())
                .firstname(dto.firstname())
                .surname(dto.surname())
                .lastname(dto.lastname())
                .email(dto.email())
                .role(dto.role())
                .build();
    }
}
