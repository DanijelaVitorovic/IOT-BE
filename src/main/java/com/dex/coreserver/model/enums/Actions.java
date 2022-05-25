package com.dex.coreserver.model.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum Actions {

    MENU_USER,
    MENU_ROLE,
    MENU_FRIDGE,

    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,
    USER_ADD_ROLE,
    USER_FIND_ALL,

    ROLE_CREATE,
    ROLE_UPDATE,
    ROLE_DELETE,
    ROLE_FIND_ALL,

    FRIDGE_CREATE,
    FRIDGE_UPDATE,
    FRIDGE_DELETE,
    FRIDGE_FIND_ALL,

    ;

    private String actionAppVersion;

    public String getActionAppVersion() {
        return actionAppVersion;
    }

    ;

}
