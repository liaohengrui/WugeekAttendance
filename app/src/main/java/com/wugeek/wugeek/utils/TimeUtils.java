package com.wugeek.wugeek.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {
    public static String toChinaTime(String timestamp) {
        Date date = new Date(new Long(timestamp));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String time = dateFormat.format(date);
        if (time.equals("1970-01-01 07:59:59")) {
            time = "     暂未离开 ";
        }
        return time;
    }

    public static long toTimestamp(String chinaData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(chinaData);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String toHours(double time) {


        return String.format("%.2f", time);
    }
}
