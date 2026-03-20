package com.app.modules.user.repository;

import com.app.core.security.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class RoleSetConverter implements AttributeConverter<Set<Role>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "USER";
        }
        return roles.stream()
                .map(Role::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashSet<>(Set.of(Role.USER));
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}