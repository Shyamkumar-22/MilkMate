package com.example.milkmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date utilities for MilkMate.
 * Format: yyyy-MM-dd for consistency with billing and milk entry.
 */
public final class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String today() {
        return formatDate(new Date());
    }

    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }

    public static String currentMonthPrefix() {
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
    }

    /** First day of month for given monthYear (yyyy-MM) */
    public static String firstDayOfMonth(String monthYear) {
        try {
            String[] parts = monthYear.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            return formatDate(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /** Last day of month for given monthYear (yyyy-MM) */
    public static String lastDayOfMonth(String monthYear) {
        try {
            String[] parts = monthYear.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 0);
            return formatDate(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /** Get monthYear for N months ago */
    public static String monthAgo(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(cal.getTime());
    }

    public static long parseToTimestamp(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}
