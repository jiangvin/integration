package model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.StringUtils;
import util.PropertyUtils;
import util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/7
 */

@Data
public class MessagePushProperty {

    @Data
    private static class AllDownProperty {
        @NonNull private String errorMessage;
        @NonNull private int errorCount;
        private int messageGroupCount = 1;
    }

    StringBuilder content = new StringBuilder();

    private boolean regularPush = false;

    private boolean allBackToNormal = true;

    private boolean allDown = true;
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

        //优化推送的内容
        service.setConnectResult(adjustErrorMessage(service.getConnectResult()));
        content.append(adjustPushMessage(service));
        updateAllStatus(service);

        //强制推送时候，那些定期推送的信息也会一起带出推送
        regularPush = true;
    }

    public void processRegularPush(Service service) {
        if (service.getPushType() != MessagePushType.REGULAR_PUSH) {
            return;
        }

        //优化推送的内容
        service.setConnectResult(adjustErrorMessage(service.getConnectResult()));
        content.append(adjustPushMessage(service));
        updateAllStatus(service);
    }

    public String generatePushContent() {
        if (StringUtils.isEmpty(content.toString())) {
            return null;
        }

        if (allBackToNormal) {
            content.append("该环境全部服务恢复正常!\n");
            return content.toString();
        }

        if (allDown) {
            AllDownProperty target = new AllDownProperty("", 0);
            target.messageGroupCount = 0;
            for (Map.Entry<String, AllDownProperty> kv : allDownMessageCountMap.entrySet()) {
                if (kv.getValue().getMessageGroupCount() > target.getMessageGroupCount()) {
                    target = kv.getValue();
                }
            }
            return String.format("该环境全部服务异常: %s[连续异常%d次]\n",
                                 target.getErrorMessage(),
                                 target.getErrorCount());
        }

        return content.toString();
    }

    private void updateAllStatus(Service service) {
        if (service.getConnectFlag()) {
            allDown = false;
            return;
        }

        allBackToNormal = false;
        if (!allDown) {
            return;
        }

        if (allDownMessageCountMap.containsKey(service.getConnectResult())) {
            allDownMessageCountMap.get(service.getConnectResult()).messageGroupCount += 1;
        } else {
            allDownMessageCountMap.put(service.getConnectResult(),
                                       new AllDownProperty(service.getConnectResult(),
                                                           service.getErrorCount()));
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
