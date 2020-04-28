package util;

import java.sql.Timestamp;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/26
 */
public class TimeUtils {
    public static long getHoursOfDay() {
        return (System.currentTimeMillis() / 1000 / 60 / 60 + 8) % 24;
    }

    public static long getMinutesOfHour() {
        return System.currentTimeMillis() / 1000 / 60 % 60;
    }

    public static Timestamp yesterday() {
        return new Timestamp(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    }

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getTimeWithOffset(long millis) {
        return new Timestamp(System.currentTimeMillis() + millis);
    }

    public static String getTimeStr(Timestamp start, Timestamp end) {
        long durationSeconds = (end.getTime() - start.getTime()) / 1000;

        long days = (durationSeconds / 60 / 60 / 24);
        long hours = (durationSeconds / 60 / 60) % 24;
        long minutes = (durationSeconds / 60) % 60;
        long seconds = durationSeconds % 60;

        String str = "";
        if (days != 0) {
            str += days + "天";

            if (hours == 0 && minutes == 0 && seconds == 0) {
                return str;
            }
        }

        if (hours != 0) {
            str += hours + "小时";

            if (minutes == 0 && seconds == 0) {
                return str;
            }
        }

        if (minutes != 0) {
            str += minutes + "分";

            if (seconds == 0) {
                return str;
            }
        }

        return str + seconds + "秒";
    }
}
