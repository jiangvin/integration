package service;

import dao.BaseDao;
import utils.MessagePushUtils;
import utils.PropertyUtils;
import model.CustomException;
import model.CustomExceptionType;
import model.MessagePushType;
import model.ServiceNotifier;

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

    private ConnectService connectService = new ConnectService();
    private BaseDao baseDao = new BaseDao();

    public void start() {
        List<ServiceNotifier> serviceNotifiers = baseDao.queryServiceNotifiers();
        if (serviceNotifiers.isEmpty()) {
            throw new CustomException(CustomExceptionType.NO_DATA, "找不到查询数据!");
        }

        //网络检查
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            try {
                String version = findVersion(serviceNotifier);
                if (version == null) {
                    serviceNotifier.setConnectResult("找不到版本信息", false);
                    continue;
                }
                serviceNotifier.setConnectResult(version, true);
            } catch (Exception e) {
                serviceNotifier.setConnectResult(e.getMessage(), false);
            }
        }

        checkVersion(serviceNotifiers);
        updateErrorCount(serviceNotifiers);
        baseDao.updateCheckLog(serviceNotifiers);
        MessagePushUtils.sendMessage(serviceNotifiers);
    }

    private void checkVersion(List<ServiceNotifier> serviceNotifiers) {
        //统计版本号
        HashMap<String, Integer> versionCountMap = new HashMap<>(16);
        serviceNotifiers.stream().filter(ServiceNotifier::getConnectFlag).forEach(i -> {
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
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            if (!serviceNotifier.getConnectFlag()) {
                continue;
            }

            if (serviceNotifier.getConnectResult().equals(maxVersion)) {
                continue;
            }

            serviceNotifier.setConnectResult(String.format("版本异常:%s (%d%%的版本为%s)",
                                                           serviceNotifier.getConnectResult(),
                                                           rate,
                                                           maxVersion), false);
        }
    }

    private void updateErrorCount(List<ServiceNotifier> serviceNotifiers) {
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            int lastCount = baseDao.queryErrorCount(serviceNotifier.getServiceId());

            //一直正常
            if (serviceNotifier.getConnectFlag() && lastCount == 0) {
                serviceNotifier.setNeedSave(false);
                continue;
            }

            //恢复正常
            if (serviceNotifier.getConnectFlag() && lastCount != 0) {
                String msg = "恢复正常";
                Timestamp startTime = baseDao.queryErrorStartTime(serviceNotifier.getServiceId());
                if (startTime != null) {
                    Timestamp endTime = new Timestamp(System.currentTimeMillis());
                    String durationMsg = String.format(",异常持续时间[%s]~[%s]", startTime.toString(), endTime.toString());
                    msg += durationMsg;
                }
                serviceNotifier.setConnectResult(msg);
                if (lastCount >= PropertyUtils.PUSH_FOR_ERROR_COUNT) {
                    serviceNotifier.setPushType(MessagePushType.FORCE_PUSH);
                }
                continue;
            }

            //异常
            if (!serviceNotifier.getConnectFlag()) {
                int errorCount = lastCount + 1;
                serviceNotifier.setErrorCount(errorCount);
                if (PropertyUtils.isTargetErrorCount(errorCount)) {
                    serviceNotifier.setPushType(MessagePushType.FORCE_PUSH);
                } else if (errorCount > PropertyUtils.PUSH_FOR_ERROR_COUNT) {
                    serviceNotifier.setPushType(MessagePushType.REGULAR_PUSH);
                }
            }
        }
    }

    private String findVersion(ServiceNotifier serviceNotifier) {
        String str = connectService.getRequest(serviceNotifier.getUrl(), String.class);
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
