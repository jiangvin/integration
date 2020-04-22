package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.DbUtils;
import util.PropertyUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */

@Slf4j
public class MemoryCheckUnit extends BaseCheckUnit {

    private static final Pattern MEMORY_USED_1 = Pattern.compile("jvm_memory_used_bytes\\{area=\"heap\",id=\"PS Old Gen\",} [^\\n]+");
    private static final Pattern MEMORY_MAX_1 = Pattern.compile("jvm_memory_max_bytes\\{area=\"heap\",id=\"PS Old Gen\",} [^\\n]+");

    /**
     * for support spring1.x
     */
    private static final Pattern MEMORY_USED_2 = Pattern.compile("jvm_memory_pool_bytes_used\\{pool=\"PS Old Gen\",} [^\\n]+");
    private static final Pattern MEMORY_MAX_2 = Pattern.compile("jvm_memory_pool_bytes_max\\{pool=\"PS Old Gen\",} [^\\n]+");

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

        service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_1));
        if (service.getOldGenMax() == null) {
            service.setOldGenMax(strConvertMemoryInt(str, MEMORY_MAX_2));
        }

        service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_1));
        if (service.getOldGenUsed() == null) {
            service.setOldGenUsed(strConvertMemoryInt(str, MEMORY_USED_2));
        }

        return service.getOldGenMax() != null && service.getOldGenUsed() != null;
    }

    private Integer strConvertMemoryInt(String str, Pattern pattern) {
        Matcher m = pattern.matcher(str);
        if (!m.find()) {
            return null;
        }

        str = m.group();
        String doubleStr = str.split("} ")[1];
        try {
            double value = Double.parseDouble(doubleStr) / 1024 / 1024;
            return (int) value;
        } catch (Exception e) {
            log.error(String.format("double %s convert error:", doubleStr), e);
            return null;
        }
    }
}
