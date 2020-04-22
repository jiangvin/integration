package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */
@Slf4j
public abstract class BaseCheckUnit {
    /**
     * 检查的主入口
     * @param services 服务列表
     */
    public void start(List<Service> services) {
        List<Service> checkList = new ArrayList<>();
        services.forEach(i -> {
            if (isCheck(i)) {
                checkList.add(i);
            }
        });

        if (checkList.isEmpty()) {
            log.warn("no service will be checked in {}", this.getClass().toString());
            return;
        }

        startCheck(checkList);
    }

    /**
     * 是否要检查该服务
     * @param service
     * @return
     */
    abstract boolean isCheck(Service service);

    /**
     * 检查的主函数
     * @param services
     */
    abstract void startCheck(List<Service> services);
}
