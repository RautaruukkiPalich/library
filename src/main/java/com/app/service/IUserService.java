package com.app.service;

import com.app.dto.UserDTO;

public interface IUserService {
    UserDTO getByID(Long id);
    Long add(UserDTO dto);
}
