package com.app.modules.user.api;

import com.app.modules.user.dto.ProfileDTO;

public interface UserProfileService {

    void editPassword(ProfileDTO.EditPassword dto);

    void editFirstname(ProfileDTO.EditFirstname dto);

    void editSurname(ProfileDTO.EditSurname dto);

    void editLastname(ProfileDTO.EditLastname dto);

    void editEmail(ProfileDTO.EditEmail dto);
}
