package org.trex.falcon.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    private static Map<String, SimpleDateFormat> sdfs = new HashMap<>();

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = sdfs.get(pattern);
        if (sdf == null) {
            sdf = new SimpleDateFormat(pattern);
            sdfs.put(pattern, sdf);
        }
        return sdf.format(date);
    }

    public static Date parse(String date) {
        return parse(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parse(String date, String pattern) {
        try {
            SimpleDateFormat sdf = sdfs.get(pattern);
            if (sdf == null) {
                sdf = new SimpleDateFormat(pattern);
                sdfs.put(pattern, sdf);
            }
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}