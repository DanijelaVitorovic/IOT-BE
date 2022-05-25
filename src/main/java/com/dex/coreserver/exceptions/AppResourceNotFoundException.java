package com.dex.coreserver.exceptions;

import com.dex.coreserver.util.DescriptionUtils;

public class AppResourceNotFoundException extends RuntimeException{

    public AppResourceNotFoundException(String key){
        super(DescriptionUtils.getErrorDescription(key));
    }
}
