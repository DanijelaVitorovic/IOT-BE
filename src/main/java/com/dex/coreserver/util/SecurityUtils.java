package com.dex.coreserver.util;

import java.util.ResourceBundle;

public class SecurityUtils {

    public static String getSecret(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        String defaultSecret = null;
        try{
            defaultSecret = tokenRB.getString("secret");
        }catch (Exception e){
            defaultSecret = "SecretKeyToGenJWTs";
        }
        return defaultSecret;
    }

    public static String getTokenPrefix(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        String defaultPrefix = null;
        try{
            defaultPrefix = tokenRB.getString("token.prefix");
        }catch (Exception e){
            defaultPrefix = "Core ";
        }
        return defaultPrefix;
    }

    public static String getHeaderString(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        String defaultHeader = null;
        try{
            defaultHeader = tokenRB.getString("header");
        }catch (Exception e){
            defaultHeader = "Authorization";
        }
        return defaultHeader;
    }

    public static Long getAccessTokenExptime(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        Long defaultExpTime = null;
        try{
            defaultExpTime = Long.parseLong(tokenRB.getString("access.token.exp.time"));
        }catch (Exception e){
            defaultExpTime = 1800000L;
        }
        return defaultExpTime;
    }

    public static Long getRefreshTokenExpTime(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        Long defaultExpTime = null;
        try{
            defaultExpTime = Long.parseLong(tokenRB.getString("refresh.token.exp.time"));
        }catch (Exception e){
            defaultExpTime = 900000L;
        }
        return defaultExpTime;
    }

    public static Long getStartRefreshTokenInterval(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        Long defaultInterval = null;
        try{
            defaultInterval = Long.parseLong(tokenRB.getString("start.refresh.interval"));
        }catch (Exception e){
            defaultInterval = 800000L;
        }
        return defaultInterval;
    }

    public static Long getCheckTokenExpInterval(){
        ResourceBundle tokenRB = ResourceBundle.getBundle("security-token");
        Long defaultInterval = null;
        try{
            defaultInterval = Long.parseLong(tokenRB.getString("check.token.exp.interval"));
        }catch (Exception e){
            defaultInterval = 660000L;
        }
        return defaultInterval;
    }

}
