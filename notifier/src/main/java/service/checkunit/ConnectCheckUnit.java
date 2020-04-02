package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import util.HttpUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class ConnectCheckUnit implements BaseCheckUnit {
    private static final String STATUS = "zcloud_service_status&";
    private static final String VERSION_TAG = "version=";
    private static final String START_TIME_TAG = "start_time=";

    private static final String MEMORY_MAX_PRE_1 = "jvm_memory_max_bytes";
    private static final String MEMORY_USED_PRE_1 = "jvm_memory_used_bytes";
    private static final String OLD_GEN_TAG_1 = "&area=\"heap\",id=\"PS Old Gen\",}";

    private static final String MEMORY_MAX_PRE_2 = "jvm_memory_pool_bytes_max";
    private static final String MEMORY_USED_PRE_2 = "jvm_memory_pool_bytes_used";
    private static final String OLD_GEN_TAG_2 = "&pool=\"PS Old Gen\",}";

    @Override
    public void start(List<Service> services) {
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

    private void findBaseInfo(Service service) {
        findStartTimeAndVersion(service);
        findMemoryInfo(service);

        if (service.getVersion() == null
                || service.getStartTime() == null
                || service.getOldGenMax() == null
                || service.getOldGenUsed() == null) {
            String keyInfo = String.format("MemoryMax:%d,MemoryUsed:%d,Version:%s,StartTime:%s",
                                           service.getOldGenMax(),
                                           service.getOldGenUsed(),
                                           service.getVersion(),
                                           service.getStartTime());

            log.error(String.format("convert info failed: %s\nresult:\n%s",
                                    service.getConnectResult(),
                                    keyInfo));
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

    private void findMemoryInfo(Service service) {
        String str = service.getConnectResult();

        service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_PRE_1 + OLD_GEN_TAG_1));
        if (service.getOldGenMax() == null) {
            service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_PRE_2 + OLD_GEN_TAG_2));
        }

        service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_PRE_1 + OLD_GEN_TAG_1));
        if (service.getOldGenUsed() == null) {
            service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_PRE_2 + OLD_GEN_TAG_2));
        }
    }

    private Integer strConvertMemoryInt(String str, String key) {
        if (!str.contains(key)) {
            return null;
        }

        String doubleStr = str.split(key)[1].split("\n")[0];
        try {
            double value = Double.parseDouble(doubleStr) / 1024 / 1024;
            return (int) value;
        } catch (Exception e) {
            log.error(String.format("double %s convert error:", doubleStr), e);
            return null;
        }
    }
}
