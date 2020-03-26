package utils;

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
}
