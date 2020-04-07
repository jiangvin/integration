package util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@Slf4j
public class PropertyUtils {

    private static PropertyUtils propertyUtils = new PropertyUtils();

    private Map<String, String> propertyMap = new HashMap<>();

    private PropertyUtils() {
        propertyMap.put("wxPostUrl", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=01a4d06b-9f42-4104-a125-9be13b46fbd3");
        propertyMap.put("dbUrl", "jdbc:mysql://192.168.5.224:3306/service-notifier?useSSL=false&characterEncoding=utf-8");
        propertyMap.put("dbUsername", "root");
        propertyMap.put("dbPassword", "Root@123");
        propertyMap.put("isDebug", "false");
        propertyMap.put("memoryRateLimit", "90");
        propertyMap.put("interval", "3");
        propertyMap.put("startHour", "9");
        propertyMap.put("endHour", "22");
    }

    public static final int PUSH_FOR_ERROR_COUNT = 3;

    public static int getInterval() {
        return convertStrToInt("interval", 3);
    }

    public static int getStartHour() {
        return convertStrToInt("startHour", 9);
    }

    public static int getEndHour() {
        return convertStrToInt("endHour", 22);
    }

    public static int getMemoryRateLimit() {
        return convertStrToInt("memoryRateLimit", 90);
    }

    public static String getWxPostUrl() {
        return propertyUtils.propertyMap.get("wxPostUrl");
    }

    public static String getDbUrl() {
        return propertyUtils.propertyMap.get("dbUrl");
    }

    public static String getDbUsername() {
        return propertyUtils.propertyMap.get("dbUsername");
    }

    public static String getDbPassword() {
        return propertyUtils.propertyMap.get("dbPassword");
    }

    public static boolean isDebug() {
        try {
            return Boolean.parseBoolean(propertyUtils.propertyMap.get("isDebug"));
        } catch (Exception e) {
            log.error("catch convert error:", e);
            return true;
        }
    }

    public static boolean isTargetErrorCount(int count) {
        return count == PUSH_FOR_ERROR_COUNT
               || count == (PUSH_FOR_ERROR_COUNT * 3 + 1)
               || count == (PUSH_FOR_ERROR_COUNT * 7 - 1);
    }

    public static void setArgs(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--") || !arg.contains("=")) {
                continue;
            }

            int keyStart = 2;
            int keyEnd = arg.indexOf("=");
            int valueStart = keyEnd + 1;
            int valueEnd = arg.length();
            if (keyEnd <= keyStart || valueEnd <= valueStart) {
                continue;
            }
            String key = arg.substring(keyStart, keyEnd);
            String value = arg.substring(valueStart, valueEnd);
            log.info("get key={},value={}", key, value);
            propertyUtils.propertyMap.put(key, value);
        }
    }

    private static int convertStrToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(propertyUtils.propertyMap.get(str));
        } catch (Exception e) {
            log.error("catch convert error:", e);
            return defaultValue;
        }
    }
}
