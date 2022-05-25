package com.dex.coreserver.util;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.AbstractDataModel;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.model.enums.StateCode;


import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;

public class Validate {

    public static void isNotNull(Object object, String exceptionMessageKey) throws AppException {
        if (object == null) {
            throw new AppException(exceptionMessageKey);
        } else if((object instanceof String) && (object.equals(""))) {
            throw new AppException(exceptionMessageKey);
        } else if((object instanceof List) && (((List) object).size() == 0)) {
            throw new AppException(exceptionMessageKey);
        } else if((object.getClass().isArray()) && (Array.getLength(object) == 0)) {
            throw new AppException(exceptionMessageKey);
        } else if((object instanceof Boolean) && !((Boolean) object)) {
            throw new AppException(exceptionMessageKey);
        }
    }

    public static void isNull(Object object, String exceptionMessageKey) throws AppException {
        if (object != null) {
            throw new AppException(exceptionMessageKey);
        }
    }

    public static void isEquals(Object firstValue, Object secondValue, String exceptionMessageKey) throws AppException{
        if(firstValue != null) {
            if(!firstValue.equals(secondValue)) {
                throw new AppException(exceptionMessageKey);
            }
        } else if (secondValue != null) {
            throw new AppException(exceptionMessageKey);
        }
    }

    public static void isNotEquals(Object firstValue, Object secondValue, String exceptionMessageKey) throws AppException{
        if(firstValue != null) {
            if( firstValue.equals(secondValue)) {
                throw new AppException(exceptionMessageKey);
            }
        } else if (secondValue == null) {
            throw new AppException(exceptionMessageKey);
        }
    }

    public static void isDateBefore(Calendar firstDate, Calendar secondDate, String exceptionMessageKey){
        int compareDate = CalendarUtils.startOfDay(firstDate).compareTo(CalendarUtils.startOfDay(secondDate));
        if (compareDate != -1){
            throw new AppException(exceptionMessageKey);
        }
    }

    public static void isDateEqual(Calendar firstDate, Calendar secondDate, String exceptionMessageKey){
        int compareDate = CalendarUtils.startOfDay(firstDate).compareTo(CalendarUtils.startOfDay(secondDate));
        if (compareDate != 0){
            throw new AppException(exceptionMessageKey);
        }
    }

    public static boolean checkUserPermission(Actions action, User user) throws AppException{
        if(user == null) return false;
        if(action == null) return false;
        if(user.myActions().isEmpty()) return false;
        if(user.myActions().contains(action)) return true;
        return false;
    }

    public static void isFileFormatNotPdfOrDocx(String format, String exceptionMessageKey){
        if(format != null) {
            if (!format.equals(".pdf") && !format.equals(".docx")) {
                throw new AppException(exceptionMessageKey);
            }
        }
    }
    public static void isFileSizeLargerThan10MB(int fileSize, String exceptionMessageKey){
        if(fileSize != 0) {
            if (fileSize > 10000000) {
                throw new AppException(exceptionMessageKey);
            }
        }
    }

    public static void isEntityOrIdNotNull(AbstractDataModel object, String exceptionMessageKey) throws AppException {
        isNotNull(object, exceptionMessageKey);
        isNotNull(object.getId(), exceptionMessageKey);
    }

    public static boolean isInPrerequisiteState (StateCode[] statuses, StateCode status) {
        boolean consistState = false;
        if(status != null) {
            for (int i = 0; i < statuses.length; i++) {
                if(status.equals(statuses[i])){
                    consistState = true;
                    break;
                }
            }
        }
        return consistState;
    }

    public static void isFileInAllowedFormat(String format, String exceptionMessage) {
        if(format != null) {
            if (!ApplicationUtils.allowedFileFormats.contains( format )) throw new AppException(exceptionMessage);
        } else {
            throw new AppException("FILE_FORMAT_IS_NULL");
        }
    }

}
