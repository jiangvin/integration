package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.HttpUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class ConnectSpringCheckUnit extends BaseCheckUnit {

    private static final Pattern STATUS = Pattern.compile("zcloud_service_status\\{.+}");
    private static final Pattern VERSION_TAG = Pattern.compile("version=\"[0-9.]+\"");
    private static final Pattern START_TIME_TAG = Pattern.compile("start_time=\"[^\"]+\"");

    @Override
    void startCheck(List<Service> services) {
        for (Service service : services) {
            try {
                String str = HttpUtils.getRequest(service.getUrl(), String.class)
                             .replace("\\\"", "\"")
                             .replace("\\n", "\n");
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
        Matcher m = STATUS.matcher(service.getConnectResult());
        if (!m.find()) {
            return;
        }
        String statusInfo = m.group();

        //找寻版本号
        m = VERSION_TAG.matcher(statusInfo);
        if (m.find()) {
            String versionInfo = m.group();
            String version = versionInfo.split("=")[1]
                             .replace("\"", "");
            service.setVersion(version);
        }

        //找寻启动时间
        m = START_TIME_TAG.matcher(statusInfo);
        if (m.find()) {
            String startTimeStr = m.group().split("=")[1].replace("\"", "");
            try {
                Timestamp startTime = Timestamp.valueOf(startTimeStr);
                service.setStartTime(startTime);
            } catch (Exception e) {
                log.error(String.format("Timestamp %s convert error:", startTimeStr), e);
            }
        }
    }
}
