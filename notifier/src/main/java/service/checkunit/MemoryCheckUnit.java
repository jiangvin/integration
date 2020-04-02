package service.checkunit;

import model.Service;
import util.DbUtils;
import util.PropertyUtils;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */
public class MemoryCheckUnit implements BaseCheckUnit {
    @Override
    public void start(List<Service> services) {
        for (Service service : services) {
            if (service.getOldGenMax() == null || service.getOldGenUsed() == null) {
                continue;
            }

            int rate = service.getOldGenUsed() * 100 / service.getOldGenMax();
            if (rate > PropertyUtils.getMemoryRateLimit()) {
                service.setConnectResult(String.format("OldGen内存占用达到%d%% (%dM / %dM)",
                                                       rate,
                                                       service.getOldGenUsed(),
                                                       service.getOldGenMax()), false);
            }
        }

        DbUtils.updateMemoryLog(services);
    }
}
