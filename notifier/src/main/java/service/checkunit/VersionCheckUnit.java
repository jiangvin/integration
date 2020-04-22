package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class VersionCheckUnit extends BaseCheckUnit {

    @Override
    void startCheck(List<Service> services) {
        HashMap<String, Integer> versionCountMap = new HashMap<>(16);
        services.forEach(i -> {
            String version = i.getVersionWithoutPatch();
            if (versionCountMap.containsKey(version)) {
                versionCountMap.put(version, versionCountMap.get(version) + 1);
            } else {
                versionCountMap.put(version, 1);
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
            if (service.getVersion() == null) {
                continue;
            }

            if (service.getVersionWithoutPatch().equals(maxVersion)) {
                continue;
            }

            String errorMsg = String.format("版本异常:%s (%d%%的版本为%s)",
                                            service.getVersion(),
                                            rate,
                                            maxVersion);
            service.setConnectResult(errorMsg, false);
            log.error("{}:{}", service.getServiceId(), errorMsg);
        }
    }

    @Override
    boolean isCheck(Service service) {
        return service.getVersion() != null;
    }
}
