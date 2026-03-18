package com.app.modules.auth.dto;

public class AuthMapper {
    public static LoginDTO toLoginDTO(AuthControllerDTO.Login form) {
        return LoginDTO.builder()
                .email(form.getEmail())
                .password(form.getPassword())
                .build();
    }

    public static RegisterDTO toRegisterDTO(AuthControllerDTO.Register form) {
        return RegisterDTO.builder()
                .firstname(form.getFirstname())
                .lastname(form.getLastname())
                .surname(form.getSurname())
                .email(form.getEmail())
                .rawPassword(form.getPassword())
                .build();
    }
}
