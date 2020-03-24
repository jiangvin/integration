package service;

import dao.BaseDao;
import model.CustomException;
import model.CustomExceptionType;
import model.ServiceNotifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/23
 */
public class HealthCheckService {

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
                String str = connectService.getRequest(serviceNotifier.getUrl(), String.class);
                if (!str.contains(VERSION_TAG)) {
                    serviceNotifier.setConnectResult("找不到版本信息", false);
                    continue;
                }
                String version = str.split("version=")[1].split(",")[0].replace("\"", "").replace("\\", "");
                if (version.length() > 5) {
                    version = version.substring(0, 5);
                }
                serviceNotifier.setConnectResult(version, true);
            } catch (Exception e) {
                serviceNotifier.setConnectResult(e.getMessage(), false);
            }
        }

        checkVersion(serviceNotifiers);
        updateErrorCount(serviceNotifiers);
        baseDao.updateCheckLog(serviceNotifiers);
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
        serviceNotifiers.stream().filter(i -> !i.getConnectFlag()).forEach(i -> {
            i.setErrorCount(baseDao.queryErrorCount(i.getServiceId()) + 1);
        });
    }
}
