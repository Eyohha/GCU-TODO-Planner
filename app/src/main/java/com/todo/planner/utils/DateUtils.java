package com.todo.planner.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }

    public static String formatDate(String date) {
        try {
            Date parsedDate = DATE_FORMAT.parse(date);
            return DATE_FORMAT.format(parsedDate);
        } catch (Exception e) {
            return getCurrentDate();
        }
    }
}
