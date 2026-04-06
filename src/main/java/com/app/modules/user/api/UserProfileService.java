package com.app.modules.user.api;

import com.app.modules.user.dto.ProfileDTO;
import lombok.NonNull;

public interface UserProfileService {

    void editPassword(ProfileDTO.@NonNull EditPassword dto);

    void editFirstname(ProfileDTO.@NonNull EditFirstname dto);

    void editSurname(ProfileDTO.@NonNull EditSurname dto);

    void editLastname(ProfileDTO.@NonNull EditLastname dto);

    void editEmail(ProfileDTO.@NonNull EditEmail dto);
}
