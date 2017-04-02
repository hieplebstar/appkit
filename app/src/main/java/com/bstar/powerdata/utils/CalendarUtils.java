package com.bstar.powerdata.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarUtils {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_LOG_FORMAT = "yyyy-MM-dd HH:mm:ss+00:00";
    public static final String DATE_SHOW_LOG_FORMAT = "MM/dd HH:mm";

    public static boolean isToday(Calendar date) {
        Calendar now = new GregorianCalendar();
        return now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == date.get(Calendar.YEAR);
    }

    public static boolean isBefore(Calendar day1, Calendar day2) {
        return day1 != null && day2 != null &&
                new GregorianCalendar(day2.get(Calendar.YEAR), day2.get(Calendar.MONTH), day2.get(Calendar.DAY_OF_MONTH)).getTimeInMillis() >
                        new GregorianCalendar(day1.get(Calendar.YEAR), day1.get(Calendar.MONTH), day1.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
    }

    public static boolean isAfter(Calendar day1, Calendar day2) {
        return day1 != null && day2 != null &&
                new GregorianCalendar(day2.get(Calendar.YEAR), day2.get(Calendar.MONTH), day2.get(Calendar.DAY_OF_MONTH)).getTimeInMillis() <
                        new GregorianCalendar(day1.get(Calendar.YEAR), day1.get(Calendar.MONTH), day1.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();

    }

    public static int dayBefore(Calendar day1, Calendar day2) {
        if(day1 == null || day2 == null) return -1;
        long diff = new GregorianCalendar(day2.get(Calendar.YEAR), day2.get(Calendar.MONTH), day2.get(Calendar.DAY_OF_MONTH)).getTimeInMillis() -
                        new GregorianCalendar(day1.get(Calendar.YEAR), day1.get(Calendar.MONTH), day1.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
        return (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
    }

    public static Calendar getCurrentTime() {
        Calendar currentCalendar = Calendar.getInstance();
        return currentCalendar;
    }

    public static Calendar getDateFromString(String date) {
        return getDateWithStringFormat(date, DATE_FORMAT);
    }

    public static Calendar getDateWithStringFormat(String date, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(date));
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String showDateWithStringFormat(Calendar date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(date.getTime());
    }

    public static String changeDateStringFormat(String date, String currentFormat, String destinationFormat) {
        Calendar calendar = getDateWithStringFormat(date, currentFormat);
        if(calendar == null) return "";
        return showDateWithStringFormat(calendar, destinationFormat);
    }
}
