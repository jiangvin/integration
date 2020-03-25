import service.HealthCheckService;
import utils.PropertyUtils;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/03/23
 */
public class Notifier {
    public static void main(String[] args) {
        PropertyUtils.setArgs(args);
        HealthCheckService healthCheckService = new HealthCheckService();
        healthCheckService.start();
    }
}