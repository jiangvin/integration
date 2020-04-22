package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.HttpUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class ConnectSpringCheckUnit extends BaseCheckUnit {
    private static final String STATUS = "zcloud_service_status&";
    private static final String VERSION_TAG = "version=";
    private static final String START_TIME_TAG = "start_time=";

    @Override
    void startCheck(List<Service> services) {
        for (Service service : services) {
            try {
                String str = HttpUtils.getRequest(service.getUrl(), String.class)
                             .replace("\\\"", "\"")
                             .replace("\\n", "\n")
                             .replace("{", "&");
                service.setConnectResult(str, true);
                findBaseInfo(service);
            } catch (Exception e) {
                service.setConnectResult(e.getMessage(), false);
            }
        }
    }

    @Override
    boolean isCheck(Service service) {
        return service.getServiceType() == ServiceType.SPRING1 || service.getServiceType() == ServiceType.SPRING2;
    }

    private void findBaseInfo(Service service) {
        findStartTimeAndVersion(service);

        if (service.getVersion() == null || service.getStartTime() == null) {
            String keyInfo = String.format("Version:%s,StartTime:%s",
                                           service.getVersion(),
                                           service.getStartTime());

            log.error("{}:convert info failed:\n{}\nresult: {}",
                      service.getServiceId(),
                      service.getConnectResult(),
                      keyInfo);
            service.setConnectResult("抓取信息失败:" + keyInfo, false);
        }
    }

    private void findStartTimeAndVersion(Service service) {
        String str = service.getConnectResult();
        if (!str.contains(STATUS)) {
            return;
        }

        String statusInfo = str.split(STATUS)[1].split("}")[0];

        //找寻版本号
        if (statusInfo.contains(VERSION_TAG)) {
            String version = statusInfo.split(VERSION_TAG)[1].split(",")[0].replace("\"", "");
            service.setVersion(version);
        }

        //找寻启动时间
        if (statusInfo.contains(START_TIME_TAG)) {
            String startTimeStr = statusInfo.split(START_TIME_TAG)[1].split(",")[0].replace("\"", "");
            try {
                Timestamp startTime = Timestamp.valueOf(startTimeStr);
                service.setStartTime(startTime);
            } catch (Exception e) {
                log.error(String.format("Timestamp %s convert error:", startTimeStr), e);
            }
        }
    }
}
