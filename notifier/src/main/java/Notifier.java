import service.HealthCheckService;
import util.DbUtils;
import util.MessagePushUtils;
import util.PropertyUtils;
import util.TimeUtils;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/03/23
 */
public class Notifier {
    public static void main(String[] args) {
        PropertyUtils.setArgs(args);

        try {
            sendStartMessage();
            HealthCheckService healthCheckService = new HealthCheckService();
            healthCheckService.start();
        } catch (Exception e) {
            MessagePushUtils.sendMessage(String.format("监控程序发生异常:%s", e.getMessage()));
        }
    }

    private static void sendStartMessage() {
        long hours = TimeUtils.getHoursOfDay();
        long minutes = TimeUtils.getMinutesOfHour();
        if (hours == 9 && minutes < PropertyUtils.getInterval()) {
            MessagePushUtils.sendMessage(String.format("监控开始运行(运行时间: 09:00 ~ 21:00) [昨天累计异常次数:%d]", DbUtils.queryYesterdayErrorCount()));
        }
    }
}