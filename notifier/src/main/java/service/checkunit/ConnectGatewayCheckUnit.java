package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.HttpUtils;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/22
 */

@Slf4j
public class ConnectGatewayCheckUnit extends BaseCheckUnit {

    private static final String GATEWAY_ID = "dbaas-apigateway";

    @Override
    boolean isCheck(Service service) {
        return service.getConnectFlag()
               && (service.getServiceType() == ServiceType.SPRING1
                   || service.getServiceType() == ServiceType.SPRING2);
    }

    @Override
    void startCheck(List<Service> services) {
        Service gateway = findGateway(services);
        if (gateway == null) {
            log.warn("can not find gateway service, ConnectGatewayCheckUnit will be ignored");
            return;
        }

        String rootPath = gateway.getUrl().split("actuator/prometheus")[0];
        for (Service service : services) {

            //跳过自己
            if (service.getServiceId().equals(GATEWAY_ID)) {
                continue;
            }

            String uri = service.getUrl().replace("http://", "");
            int begin = uri.indexOf("/");
            if (begin == -1) {
                log.warn("Invalid uri:{}, ignore it!", uri);
                continue;
            }
            uri = uri.substring(begin + 1);

            String path = rootPath;
            if (!uri.startsWith(service.getServiceId())) {
                path += service.getServiceId() + "/";
            }
            path += uri;
            try {
                HttpUtils.getRequest(path, String.class);
            } catch (Exception e) {
                log.error("request error:", e);
                service.setConnectResult("和注册中心断开", false);
            }
        }
    }

    private Service findGateway(List<Service> services) {
        return services.stream().filter(i -> i.getServiceId().equals(GATEWAY_ID))
               .findFirst().orElse(null);
    }
}
