package service;

import lombok.extern.slf4j.Slf4j;
import model.CustomException;
import model.CustomExceptionType;
import model.MessagePushType;
import model.Service;
import service.checkunit.BaseCheckUnit;
import service.checkunit.ConnectCheckUnit;
import service.checkunit.MemoryCheckUnit;
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
        checkUnitList.add(new MemoryCheckUnit());
    }

    public void start() {
        List<Service> services = DbUtils.queryServices();
        if (services.isEmpty()) {
            throw new CustomException(CustomExceptionType.NO_DATA, "找不到服务数据!");
        }
        log.info("Find service count: {}", services.size());

        sendStartMessage(services);
        checkUnitList.forEach(i -> i.start(services));

        updateErrorCount(services);
        DbUtils.updateCheckLog(services);
        MessagePushUtils.sendMessage(services);
        sendEndMessage(services);
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
                    String durationMsg = String.format(",异常持续时间:%s", TimeUtils.getTimeStr(startTime, TimeUtils.now()));
                    msg += durationMsg;
                }
                service.setConnectResult(msg);
                if (lastCount >= PropertyUtils.MIN_PUSH_FOR_ERROR_COUNT) {
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
                } else if (errorCount > PropertyUtils.MIN_PUSH_FOR_ERROR_COUNT) {
                    service.setPushType(MessagePushType.REGULAR_PUSH);
                }
            }
        }
    }

    private void sendStartMessage(List<Service> services) {
        long hours = TimeUtils.getHoursOfDay();
        long minutes = TimeUtils.getMinutesOfHour();

        if (hours == PropertyUtils.getStartHour() && minutes < PropertyUtils.getInterval()) {
            MessagePushUtils.sendMessage(String.format("监控开始运行\n服务数量:%d\n运行时间: %02d:00 ~ %02d:00\n[昨天累计异常次数:%d]",
                                                       services.size(),
                                                       PropertyUtils.getStartHour(),
                                                       PropertyUtils.getEndHour(),
                                                       DbUtils.queryYesterdayErrorCount()));
        }
    }

    private void sendEndMessage(List<Service> services) {
        long hours = TimeUtils.getHoursOfDay();
        long minutes = TimeUtils.getMinutesOfHour();
        if (hours != (PropertyUtils.getEndHour() - 1) || minutes < (60 - PropertyUtils.getInterval())) {
            return;
        }

        //统计错误数量
        int errorCount = 0;
        for (Service service : services) {
            if (!service.getConnectFlag()) {
                ++errorCount;
            }
        }

        if (errorCount == 0) {
            MessagePushUtils.sendMessage("当日监控结束，目前所有服务都正常");
        } else {
            MessagePushUtils.sendMessage(String.format("当日监控结束，目前仍有%d%%的服务处于异常状态(%d / %d)",
                                                       errorCount * 100 / services.size(),
                                                       errorCount,
                                                       services.size()));
        }
    }
}
