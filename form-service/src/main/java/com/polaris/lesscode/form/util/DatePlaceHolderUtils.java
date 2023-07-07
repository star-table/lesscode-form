package com.polaris.lesscode.form.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日期占位符解析工具
 *
 */
public class DatePlaceHolderUtils {

    private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static List<String> today(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> yesterday(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR_OF_DAY, -24);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> tomorrow(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR_OF_DAY, 24);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> thisWeek(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.DAY_OF_YEAR, 6);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> thisMonth(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> lastMonth(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, -2);
        c.set(Calendar.DAY_OF_MONTH, 1);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> nextMonth(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, 2);
        c.set(Calendar.DAY_OF_MONTH, 1);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> lastWeek(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, -7);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.DAY_OF_YEAR, 6);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> nextWeek(){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, 7);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.DAY_OF_YEAR, 6);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static List<String> nDay(int n){
        List<String> results = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, n);
        results.add(sdf.format(c.getTime()));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        results.add(sdf.format(c.getTime()));
        return results;
    }

    public static void main(String[] args) {
        System.out.println(today());
        System.out.println(yesterday());
        System.out.println(tomorrow());
        System.out.println(thisWeek());
        System.out.println(thisMonth());
        System.out.println(lastMonth());
        System.out.println(nextMonth());
        System.out.println(lastWeek());
        System.out.println(nextWeek());
        System.out.println(nDay(1));
        System.out.println(nDay(-1));
    }

}
