package com.dex.coreserver.exceptions;
import com.dex.coreserver.util.DescriptionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvalidLoginResponse {
    private String username;
    private String password;

    public InvalidLoginResponse() {
        this.username = DescriptionUtils.getErrorDescription("WRONG_USERNAME_OR_PASSWORD");
        this.password = " ";
    }
}
