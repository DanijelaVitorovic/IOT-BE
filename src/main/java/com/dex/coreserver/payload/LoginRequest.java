package com.dex.coreserver.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class LoginRequest {
    @NotBlank(message = "{notblank.username}")
    private String username;
    @NotBlank(message = "{notblank.password}")
    private String password;
}
