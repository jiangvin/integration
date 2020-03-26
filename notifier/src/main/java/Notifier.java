import service.HealthCheckService;
import utils.MessagePushUtils;
import utils.PropertyUtils;
import utils.TimeUtils;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/03/23
 */
public class Notifier {
    public static void main(String[] args) {
        long hours = TimeUtils.getHoursOfDay();
        long minutes = TimeUtils.getMinutesOfHour();
        if (hours == 9 && minutes < PropertyUtils.REGULAR_PUSH_MINUTES_OF_HOUR) {
            MessagePushUtils.sendMessage("监控开始运行(运行时间: 09:00 ~ 21:00)");
        }

        PropertyUtils.setArgs(args);
        try {
            HealthCheckService healthCheckService = new HealthCheckService();
            healthCheckService.start();
        } catch (Exception e) {
            MessagePushUtils.sendMessage(String.format("监控程序发生异常:%s", e.getMessage()));
        }
    }
}