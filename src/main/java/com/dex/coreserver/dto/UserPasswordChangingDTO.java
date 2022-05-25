package com.dex.coreserver.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserPasswordChangingDTO {
    @NotBlank(message = "{NotBlank.userPasswordChangingDTO.oldPassword}")
    String oldPassword;
    @NotBlank(message = "{NotBlank.userPasswordChangingDTO.newPassword}")
    String newPassword;
    @NotBlank(message = "{NotBlank.userPasswordChangingDTO.confirmNewPassword}")
    String confirmNewPassword;
}
