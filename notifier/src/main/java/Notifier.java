import service.HealthCheckService;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/03/23
 */
public class Notifier {
    public static void main(String[] args) {
        HealthCheckService healthCheckService = new HealthCheckService();
        healthCheckService.start();
    }
}