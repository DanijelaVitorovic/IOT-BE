package com.dex.coreserver.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountLockedResponse {
    private String message;
    public AccountLockedResponse() {
        message = "Nalog je NEAKTIVAN, kontaktirajte administratora!";
    }
}
