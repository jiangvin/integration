package service.checkunit;

import model.Service;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/2
 */
public interface BaseCheckUnit {
    /**
     * 检查的主函数
     * @param services 服务列表
     */
    void start(List<Service> services);
}
