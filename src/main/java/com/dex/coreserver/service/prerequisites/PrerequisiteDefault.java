package com.dex.coreserver.service.prerequisites;

import org.springframework.stereotype.Component;

@Component
public class PrerequisiteDefault implements PrerequisiteService {

    @Override
    public String getAppName() {
        return "default";
    }


}
