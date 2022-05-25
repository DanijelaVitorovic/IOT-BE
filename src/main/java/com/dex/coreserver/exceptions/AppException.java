package com.dex.coreserver.exceptions;

import com.dex.coreserver.util.DescriptionUtils;
import org.springframework.validation.FieldError;

public class AppException extends RuntimeException {

    public AppException(String key) {
        super(getMessage(key));
    }

    public AppException(String key, Exception e) {
        super(getUnknownErrorMessage(key, e));
    }

    public AppException(String key, String additionalText) {
        super(getMessage(key) + ": " + additionalText);
    }

    public AppException(String message, FieldError error) {
        super(message);
    }

    private static String getMessage(String key) {
        return DescriptionUtils.getErrorDescription(key);
    }

    private static String getUnknownErrorMessage(String key, Exception error) {
        return getMessage(key) + error.toString();
    }
}
