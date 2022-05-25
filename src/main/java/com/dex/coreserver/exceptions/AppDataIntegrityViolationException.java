package com.dex.coreserver.exceptions;

import com.dex.coreserver.util.DescriptionUtils;

public class AppDataIntegrityViolationException extends RuntimeException{

    public AppDataIntegrityViolationException(String key){
        super(DescriptionUtils.getErrorDescription(key));
    }
}
