import service.HealthCheckService;
import util.MessagePushUtils;
import util.PropertyUtils;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/03/23
 */
public class Notifier {
    public static void main(String[] args) {
        PropertyUtils.setArgs(args);
        try {
            HealthCheckService healthCheckService = new HealthCheckService();
            healthCheckService.start();
        } catch (Exception e) {
            MessagePushUtils.sendMessage(String.format("监控程序发生异常:%s", e.getMessage()));
        }
    }
}