package service;

import model.CustomException;
import model.CustomExceptionType;
import model.MessagePushType;
import model.Service;
import util.DbUtils;
import util.HttpUtils;
import util.MessagePushUtils;
import util.PropertyUtils;
import util.TimeUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/23
 */
public class HealthCheckService {

    private static final String BUILD_DATE_TAG = "build_date=";
    private static final String VERSION_TAG = "version=";

    public void start() {
        List<Service> services = DbUtils.queryServices();
        if (services.isEmpty()) {
            throw new CustomException(CustomExceptionType.NO_DATA, "找不到服务数据!");
        }

        //网络检查
        for (Service service : services) {
            try {
                String version = findVersion(service);
                if (version == null) {
                    service.setConnectResult("找不到版本信息", false);
                    continue;
                }
                service.setConnectResult(version, true);
            } catch (Exception e) {
                service.setConnectResult(e.getMessage(), false);
            }
        }

        checkVersion(services);
        updateErrorCount(services);
        DbUtils.updateCheckLog(services);
        MessagePushUtils.sendMessage(services);
    }

    private void checkVersion(List<Service> services) {
        //统计版本号
        HashMap<String, Integer> versionCountMap = new HashMap<>(16);
        services.stream().filter(Service::getConnectFlag).forEach(i -> {
            if (versionCountMap.containsKey(i.getConnectResult())) {
                versionCountMap.put(i.getConnectResult(), versionCountMap.get(i.getConnectResult()) + 1);
            } else {
                versionCountMap.put(i.getConnectResult(), 1);
            }
        });

        if (versionCountMap.isEmpty()) {
            return;
        }


        int total = 0;
        int maxVersionCount = 0;
        String maxVersion = "";

        for (Map.Entry<String, Integer> kv : versionCountMap.entrySet()) {
            total += kv.getValue();
            if (kv.getValue() > maxVersionCount) {
                maxVersionCount = kv.getValue();
                maxVersion = kv.getKey();
            }
        }

        int rate = maxVersionCount * 100 / total;
        for (Service service : services) {
            if (!service.getConnectFlag()) {
                continue;
            }

            if (service.getConnectResult().equals(maxVersion)) {
                continue;
            }

            service.setConnectResult(String.format("版本异常:%s (%d%%的版本为%s)",
                                                   service.getConnectResult(),
                                                   rate,
                                                   maxVersion), false);
        }
    }

    private void updateErrorCount(List<Service> services) {
        for (Service service : services) {
            int lastCount = DbUtils.queryErrorCount(service.getServiceId());

            //一直正常
            if (service.getConnectFlag() && lastCount == 0) {
                service.setNeedSave(false);
                continue;
            }

            //恢复正常
            if (service.getConnectFlag() && lastCount != 0) {
                String msg = "恢复正常";
                Timestamp startTime = DbUtils.queryErrorStartTime(service.getServiceId());
                if (startTime != null) {
                    String durationMsg = String.format(",异常持续时间[%s]~[%s]", startTime.toString(), TimeUtils.now().toString());
                    msg += durationMsg;
                }
                service.setConnectResult(msg);
                if (lastCount >= PropertyUtils.PUSH_FOR_ERROR_COUNT) {
                    service.setPushType(MessagePushType.FORCE_PUSH);
                }
                continue;
            }

            //异常
            if (!service.getConnectFlag()) {
                int errorCount = lastCount + 1;
                service.setErrorCount(errorCount);
                if (PropertyUtils.isTargetErrorCount(errorCount)) {
                    service.setPushType(MessagePushType.FORCE_PUSH);
                } else if (errorCount > PropertyUtils.PUSH_FOR_ERROR_COUNT) {
                    service.setPushType(MessagePushType.REGULAR_PUSH);
                }
            }
        }
    }

    private String findVersion(Service service) {
        String str = HttpUtils.getRequest(service.getUrl(), String.class);
        if (!str.contains(BUILD_DATE_TAG)) {
            return null;
        }

        String versionStr = str.split(BUILD_DATE_TAG)[1];
        if (!versionStr.contains(VERSION_TAG)) {
            return null;
        }

        String version = versionStr.split(VERSION_TAG)[1].split(",")[0].replace("\"", "").replace("\\", "");
        if (version.length() > 5) {
            version = version.substring(0, 5);
        }
        return version;
    }
}
