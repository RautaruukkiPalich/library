package com.app.modules.user.utils;

import com.app.core.security.rbac.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class RoleNameConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ?
                Role.USER.getAuthority() :
                role.getAuthority();
    }

    @Override
    public Role convertToEntityAttribute(String name) {
        return Role.extractRole(name).orElse(Role.USER);
    }
}