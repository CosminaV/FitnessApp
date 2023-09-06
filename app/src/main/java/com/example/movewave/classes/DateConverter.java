package com.example.movewave.classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static Calendar calendar = Calendar.getInstance();

    public static String fromDate(Date date) {
        if(date == null) {
            return null;
        }
        return dayFormat.format(date);
    }

    public static String fromTime(Date date) {
        if(date == null){
            return null;
        }
        return timeFormat.format(date);
    }

    public static Date fromInts(int year, int month, int day, int hourOfDay, int minute) {
        calendar.set(year, month, day, hourOfDay, minute);
        return calendar.getTime();
    }
}
