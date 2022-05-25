package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.util.DescriptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Locale;

@Service
public class MapValidationErrorService {
    public void MapValidationService(BindingResult result){
        MessageSource messageSource = DescriptionUtils.messageSource();
        String locale = DescriptionUtils.getLocale();
        if(result.hasErrors() && !result.getFieldErrors().isEmpty()){
            FieldError error = result.getFieldErrors().get( 0 );
            String message = messageSource.getMessage(error, new Locale(locale));
            throw new AppException(message, error);
        }
    }
}
