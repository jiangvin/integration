package service;

import lombok.extern.slf4j.Slf4j;
import model.CustomException;
import model.CustomExceptionType;
import model.MessagePushType;
import model.Service;
import service.checkunit.BaseCheckUnit;
import service.checkunit.ConnectCheckUnit;
import service.checkunit.VersionCheckUnit;
import util.DbUtils;
import util.MessagePushUtils;
import util.PropertyUtils;
import util.TimeUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/23
 */

@Slf4j
public class HealthCheckService {
    private List<BaseCheckUnit> checkUnitList;

    public HealthCheckService() {
        checkUnitList = new ArrayList<>();
        checkUnitList.add(new ConnectCheckUnit());
        checkUnitList.add(new VersionCheckUnit());
    }

    public void start() {
        List<Service> services = DbUtils.queryServices();
        if (services.isEmpty()) {
            throw new CustomException(CustomExceptionType.NO_DATA, "找不到服务数据!");
        }

        log.info("Find service count: {}", services.size());
        checkUnitList.forEach(i -> i.start(services));

        updateErrorCount(services);
        DbUtils.updateCheckLog(services);
        MessagePushUtils.sendMessage(services);
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
}
