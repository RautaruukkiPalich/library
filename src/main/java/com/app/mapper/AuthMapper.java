package com.app.mapper;

import com.app.dto.AuthDTO;
import com.app.dto.UserDTO;
import com.app.dto.controller.ControllerAuthDTO;

public class AuthMapper {
    public static AuthDTO toDTO(ControllerAuthDTO.Login form){
        return AuthDTO.builder()
                .email(form.getEmail())
                .password(form.getPassword())
                .build();
    }

    public static UserDTO toDTO(ControllerAuthDTO.Register form){
        return UserDTO.builder()
                .firstname(form.getFirstname())
                .lastname(form.getLastname())
                .surname(form.getSurname())
                .email(form.getEmail())
                .rawPassword(form.getPassword())
                .build();
    }
}
