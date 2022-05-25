package com.dex.coreserver.exceptions;

import com.dex.coreserver.util.DescriptionUtils;

public class AppNoDataFoundException extends RuntimeException{

    public AppNoDataFoundException(String key){
        super(DescriptionUtils.getErrorDescription(key));
    }

}
