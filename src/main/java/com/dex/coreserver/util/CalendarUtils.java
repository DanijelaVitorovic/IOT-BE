package com.dex.coreserver.util;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
    public static Calendar endOfDay(Calendar date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = (Calendar)date.clone();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
        }
        return calendar;
    }
    public static Calendar startOfDay(Calendar date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = (Calendar)date.clone();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
        }
        return calendar;
    }

    public static Calendar addDays(Calendar date, int amount) {
        Calendar calendar = null;
        if (date != null) {
            calendar = (Calendar)date.clone();
            calendar.add(Calendar.DAY_OF_MONTH, amount);
        }
        return calendar;
    }

    public static Calendar addMonths(Calendar date, int amount) {
        Calendar calendar = null;
        if (date != null) {
            calendar = (Calendar)date.clone();
            calendar.add(Calendar.MONTH, amount);
        }
        return calendar;
    }

    public static Calendar addYears(Calendar date, int amount) {
        Calendar calendar = null;
        if (date != null) {
            calendar = (Calendar)date.clone();
            calendar.add(Calendar.YEAR, amount);
        }
        return calendar;
    }

    public static Calendar getInstance(int dd, int mm, int yyyy) {
        Calendar returnValue = Calendar.getInstance();
        returnValue.set(yyyy, mm-1, dd);
        return returnValue;
    }

    public static String toString(Calendar date) {
        return toString(date, "dd.MM.yyyy HH:mm:ss.SSS") ;
    }
    public static String toString(Calendar date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format) ;
        return sdf.format(date.getTime());
    }
    public static Calendar firstInMonth(Calendar calendar){
        Calendar returnValue = null;
        if(calendar != null){
            returnValue = (Calendar) calendar.clone();
            returnValue.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MILLISECOND, 000);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MINUTE, 00);
        }
        return returnValue;
    }

    public static Calendar lastInMonth(Calendar calendar){
        Calendar returnValue = null;
        if(calendar != null){
            returnValue = (Calendar) calendar.clone();
            returnValue.set(Calendar.DAY_OF_MONTH, returnValue.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
        }
        return returnValue;
    }
    public static Calendar fistDayInYear(Calendar calendar){
        Calendar returnValue = null;
        if(calendar != null){
            returnValue = (Calendar) calendar.clone();
            returnValue.set(Calendar.DAY_OF_MONTH, returnValue.getActualMinimum(Calendar.DAY_OF_MONTH));
            returnValue.set(Calendar.MONTH, returnValue.getActualMinimum(Calendar.MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MILLISECOND, 000);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MINUTE, 00);
        }
        return returnValue;
    }

    public static Calendar lastDayInYear(Calendar calendar){
        Calendar returnValue = null;
        if(calendar != null){
            returnValue = (Calendar) calendar.clone();
            returnValue.set(Calendar.MONTH, returnValue.getActualMaximum(Calendar.MONTH));
            returnValue.set(Calendar.DAY_OF_MONTH, returnValue.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
        }
        return returnValue;
    }

    public static int currentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String currentDateInFormatDDMMYYYY(){
        return  Calendar.getInstance().get(Calendar.DATE) + "." + Calendar.getInstance().get(Calendar.MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String currentDateAntTime(){
        return  Calendar.getInstance().get(Calendar.DATE) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1)+ "." + Calendar.getInstance().get(Calendar.YEAR)
                + "-" + Calendar.getInstance().get(Calendar.HOUR)+":"+Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static long betweenDates(Date firstDate, Date secondDate){
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }

}
