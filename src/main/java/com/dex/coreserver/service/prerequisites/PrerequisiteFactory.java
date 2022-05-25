package com.dex.coreserver.service.prerequisites;

import com.dex.coreserver.util.DescriptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrerequisiteFactory {

    private final Map<String, PrerequisiteService> myServiceCache = new HashMap<>();

    @Autowired
    private PrerequisiteFactory(List<PrerequisiteService> prerequisiteServices){
        for(PrerequisiteService service : prerequisiteServices) {
            if(DescriptionUtils.getAppName().equals(service.getAppName())) {
                myServiceCache.put(service.getAppName(), service);
            }
        }
    }

    public PrerequisiteService getService(String appName) {
        PrerequisiteService service = myServiceCache.get(appName);
        if(service == null) throw new RuntimeException("Unknown app name: " + appName);
        return service;
    }

}
