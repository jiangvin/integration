package model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.StringUtils;
import util.PropertyUtils;
import util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/7
 */

public class MessagePushProperty {

    @Data
    private static class AllDownProperty {
        @NonNull private String errorMessage;
        @NonNull private int errorCount;
        private int messageGroupCount = 1;
    }

    @Getter
    private List<String> mentionedList = new ArrayList<>();

    @Getter
    private boolean regularPush = false;

    private StringBuilder content = new StringBuilder();

    private PushStatus pushStatus = PushStatus.NONE;

    private Map<String, AllDownProperty> allDownMessageCountMap = new HashMap<>(16);

    public MessagePushProperty() {
        //是否是整点播报
        long minutesOfHour = TimeUtils.getMinutesOfHour();
        if (minutesOfHour < PropertyUtils.getInterval()) {
            regularPush = true;
        }
    }

    public void processForcePush(Service service) {
        if (service.getPushType() != MessagePushType.FORCE_PUSH) {
            return;
        }
        baseProcessPush(service);

        //强制推送时候，那些定期推送的信息也会一起带出推送
        regularPush = true;
    }

    public void processRegularPush(Service service) {
        if (service.getPushType() != MessagePushType.REGULAR_PUSH) {
            return;
        }
        baseProcessPush(service);
    }

    private void baseProcessPush(Service service) {
        //优化推送的内容
        service.setConnectResult(adjustErrorMessage(service.getConnectResult()));
        content.append(adjustPushMessage(service));
        updateAllStatus(service);

        //提醒负责人,只在错误数量低于20的推送时提醒
        if (!service.getConnectFlag()
                && service.getCoPhone() != null
                && service.getErrorCount() <= PropertyUtils.MAX_PUSH_FOR_ERROR_COUNT
                && !mentionedList.contains(service.getCoPhone())) {
            mentionedList.add(service.getCoPhone());
        }
    }

    public String generatePushContent(int totalCount) {
        if (StringUtils.isEmpty(content.toString())) {
            return null;
        }

        int contentLines = content.toString().split("\n").length;

        if (pushStatus == PushStatus.BACK_TO_NORMAL) {
            String firstLine =  content.toString().split("\n")[0];

            if (contentLines == totalCount && firstLine.contains(",")) {
                //全部服务同一时间恢复正常
                return "该环境全部服务恢复正常," + firstLine.split(",")[1];
            } else {
                content.append("该环境全部服务恢复正常!\n");
                return content.toString();
            }
        }

        if (pushStatus == PushStatus.DOWN && contentLines == totalCount) {
            AllDownProperty target = new AllDownProperty("", 0);
            target.messageGroupCount = 0;
            for (Map.Entry<String, AllDownProperty> kv : allDownMessageCountMap.entrySet()) {
                if (kv.getValue().getMessageGroupCount() > target.getMessageGroupCount()) {
                    target = kv.getValue();
                }
            }

            //全部环境都异常并且通知列表里面有超过一个人时提醒所有人
            if (mentionedList.size() > 1) {
                mentionedList.clear();
                mentionedList.add("@all");
            }

            return String.format("该环境全部服务异常: %s[连续异常%d次]\n",
                                 target.getErrorMessage(),
                                 target.getErrorCount());
        }

        return content.toString();
    }

    private void updateAllStatus(Service service) {
        if (service.getConnectFlag()) {
            updatePushStatus(PushStatus.BACK_TO_NORMAL);
            return;
        }

        updatePushStatus(PushStatus.DOWN);
        if (this.pushStatus != PushStatus.DOWN) {
            return;
        }

        if (allDownMessageCountMap.containsKey(service.getConnectResult())) {
            AllDownProperty allDownProperty = allDownMessageCountMap.get(service.getConnectResult());
            allDownProperty.messageGroupCount += 1;

            //更新错误数量，整合错误时错误数量为最小值
            if (service.getErrorCount() < allDownProperty.getErrorCount()) {
                allDownProperty.setErrorCount(service.getErrorCount());
            }
        } else {
            allDownMessageCountMap.put(service.getConnectResult(),
                                       new AllDownProperty(service.getConnectResult(),
                                                           service.getErrorCount()));
        }
    }

    private void updatePushStatus(PushStatus pushStatus) {
        if (this.pushStatus == pushStatus) {
            return;
        }

        if (this.pushStatus == PushStatus.NONE) {
            this.pushStatus = pushStatus;
        } else {
            this.pushStatus = PushStatus.MIXED;
        }
    }

    private String adjustPushMessage(Service service) {
        if (service.getConnectFlag()) {
            return String.format("%s: %s\n", service.getServiceId(), service.getConnectResult());
        }

        return String.format("%s: %s[连续异常%d次]\n",
                             service.getServiceId(),
                             service.getConnectResult(),
                             service.getErrorCount());
    }

    private String adjustErrorMessage(String error) {
        if (error.contains("ConnectException: Connection timed out")) {
            return "连接超时";
        }

        if (error.contains("nested exception is java.net.ConnectException: Connection refused")) {
            return "服务未启动";
        }

        if (error.contains("Exception: ")) {
            String[] infos = error.split("Exception: ");
            return infos[infos.length - 1];
        }
        return error;
    }
}
