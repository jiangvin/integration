package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/9
 */

@Slf4j
public class RequestCheckUnit implements BaseCheckUnit {

    private static final String REQUEST_COUNT_KEY = "tomcat_global_request_seconds_count";

    private static final String REQUEST_SECONDS_KEY = "tomcat_global_request_seconds_sum";

    @Override
    public void start(List<Service> services) {
        int totalRequestCount = 0;
        int totalRequestSeconds = 0;

        for (Service service : services) {
            if (!service.getConnectFlag()) {
                continue;
            }

            if (!findRequestInfo(service)) {
                log.error("{}: can not find request info ({} / {}) from:\n{}",
                          service.getServiceId(),
                          service.getRequestCount(),
                          service.getRequestSeconds(),
                          service.getConnectResult());
                service.setConnectResult("找不到网络连接信息", false);
                continue;
            }

            totalRequestCount += service.getRequestCount();
            totalRequestSeconds += service.getRequestSeconds();
        }

        for (Service service : services) {
            if (service.getRequestCount() == null || service.getRequestSeconds() == null) {
                continue;
            }

            log.info("{}: request count rate:{}%, request seconds rate:{}%",
                     service.getServiceId(),
                     service.getRequestCount() * 100 / totalRequestCount,
                     service.getRequestSeconds() * 100 / totalRequestSeconds);
        }
    }

    private boolean findRequestInfo(Service service) {
        String str = service.getConnectResult();

        service.setRequestCount(strConvertRequestInt(str, REQUEST_COUNT_KEY));
        service.setRequestSeconds(strConvertRequestInt(str, REQUEST_SECONDS_KEY));

        return service.getRequestCount() != null && service.getRequestSeconds() != null;
    }

    private Integer strConvertRequestInt(String str, String key) {
        if (!str.contains(key)) {
            return null;
        }

        String splitInfo = str.split(key)[1];
        if (!splitInfo.contains("} ")) {
            return null;
        }

        String doubleStr = splitInfo.split("} ")[1].split("\n")[0];

        try {
            double value = Double.parseDouble(doubleStr);
            return (int) value;
        } catch (Exception e) {
            log.error(String.format("double %s convert error:", doubleStr), e);
            return null;
        }
    }
}
