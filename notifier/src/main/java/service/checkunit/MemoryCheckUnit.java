package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.DbUtils;
import util.PropertyUtils;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class MemoryCheckUnit extends BaseCheckUnit {
    private static final String MEMORY_MAX_PRE_1 = "jvm_memory_max_bytes";
    private static final String MEMORY_USED_PRE_1 = "jvm_memory_used_bytes";
    private static final String OLD_GEN_TAG_1 = "&area=\"heap\",id=\"PS Old Gen\",}";

    private static final String MEMORY_MAX_PRE_2 = "jvm_memory_pool_bytes_max";
    private static final String MEMORY_USED_PRE_2 = "jvm_memory_pool_bytes_used";
    private static final String OLD_GEN_TAG_2 = "&pool=\"PS Old Gen\",}";

    @Override
    void startCheck(List<Service> services) {
        for (Service service : services) {
            if (!findMemoryInfo(service)) {
                log.error("{}: can not find memory info ({} / {}) from:\n{}",
                          service.getServiceId(),
                          service.getOldGenUsed(),
                          service.getOldGenMax(),
                          service.getConnectResult());
                service.setConnectResult("找不到内存信息", false);
                continue;
            }

            int rate = service.getOldGenUsed() * 100 / service.getOldGenMax();
            if (rate > PropertyUtils.getMemoryRateLimit()) {
                String errorMsg = String.format("OldGen内存占用达到%d%% (%dM / %dM)",
                                                rate,
                                                service.getOldGenUsed(),
                                                service.getOldGenMax());
                service.setConnectResult(errorMsg, false);
                log.error("{}:{}", service.getServiceId(), errorMsg);
            }

        }
        DbUtils.updateMemoryLog(services);
    }

    @Override
    boolean isCheck(Service service) {
        return service.getConnectFlag()
               && (service.getServiceType() == ServiceType.SPRING1
                   || service.getServiceType() == ServiceType.SPRING2);
    }

    private boolean findMemoryInfo(Service service) {
        String str = service.getConnectResult();

        service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_PRE_1 + OLD_GEN_TAG_1));
        if (service.getOldGenMax() == null) {
            service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_PRE_2 + OLD_GEN_TAG_2));
        }

        service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_PRE_1 + OLD_GEN_TAG_1));
        if (service.getOldGenUsed() == null) {
            service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_PRE_2 + OLD_GEN_TAG_2));
        }

        return service.getOldGenMax() != null && service.getOldGenUsed() != null;
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
