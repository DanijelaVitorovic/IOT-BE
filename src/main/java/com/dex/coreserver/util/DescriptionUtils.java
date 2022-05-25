package com.dex.coreserver.util;


import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class DescriptionUtils {
    public static String errorDescriptionPath = "desc/" + getAppName() + "/error_description";
    public static String actionDescriptionPath = "desc/" + getAppName() + "/action_description";
    public static String attributeValidationPath = "desc/" + getAppName() + "/attribute_validation";
    public static String responseDescriptionPath = "desc/" + getAppName() + "/response_description";

    @NotNull
    public static String getErrorDescription(String key){
        Locale locale = new Locale(getLocale());
        ResourceBundle errorResourceBundle = ResourceBundle.getBundle(errorDescriptionPath, locale);
        String errorMessage = null;
        try{
            errorMessage = errorResourceBundle.getString(key);
        }catch(Exception ex){
            String unknownKeyMessagePart = errorResourceBundle.getString("WRONG_ERROR_KEY");
            errorMessage = unknownKeyMessagePart + key;
        }
        return convertStringExplicitlyToUTF8(errorMessage);
    }

    public static String getLocale(){
        if(ThreadContext.get("locale") != null && !ThreadContext.get("locale").isEmpty()) {
            return ThreadContext.get("locale");
        }
        ResourceBundle applicationRB = ResourceBundle.getBundle("application");
        String defaultLocale = null;
        try{
            defaultLocale = applicationRB.getString("app.locale");
        }catch (Exception e){
            defaultLocale = "sr";
        }
        return defaultLocale;
    }

    @Bean
    public static MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:" + attributeValidationPath + "_" + getLocale());
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public static String getDescription(String key, String path){
        Locale locale = new Locale(getLocale());
        ResourceBundle descriptionResourceBundle = ResourceBundle.getBundle(path, locale);
        String descriptionMessage = null;
        try{
            descriptionMessage = descriptionResourceBundle.getString(key);
        }catch(Exception ex){
            String unknownKeyMessagePart = descriptionResourceBundle.getString("WRONG_ERROR_KEY");
            descriptionMessage = unknownKeyMessagePart + key;
        }
        return convertStringExplicitlyToUTF8(descriptionMessage);
    }

    public static String getActionDescription (String key) {
        return DescriptionUtils.getDescription(key, DescriptionUtils.actionDescriptionPath);
    }

    public static String getResponseDescription (String key){
        return DescriptionUtils.getDescription(key, DescriptionUtils.responseDescriptionPath);
    }

    public static String getAppName(){
        ResourceBundle applicationRB = ResourceBundle.getBundle("application");
        String defaultLocale;
        try{
            defaultLocale = applicationRB.getString("app.name");
        }catch (Exception e){
            defaultLocale = "default";
        }
        return defaultLocale;
    }

    public static String getAppVersion(){
        ResourceBundle applicationRB = ResourceBundle.getBundle("application");
        String defaultLocale;
        try{
            defaultLocale = applicationRB.getString("app.version");
        }catch (Exception e){
            defaultLocale = "1.1";
        }
        return defaultLocale;
    }

    private static String convertStringExplicitlyToUTF8(String text){
        try {
            text = new String( text.getBytes( "ISO-8859-1" ), "UTF-8" );
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return text;
    }

}




