package util;

import lombok.extern.slf4j.Slf4j;
import model.MessagePushType;
import model.Service;
import model.WxMessage;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@Slf4j
public class MessagePushUtils {
    private MessagePushUtils() {

    }

    public static void sendMessage(List<Service> services) {
        boolean regularPush = false;
        long minutesOfHour = TimeUtils.getMinutesOfHour();
        if (minutesOfHour < PropertyUtils.REGULAR_PUSH_MINUTES_OF_HOUR) {
            regularPush = true;
        }

        boolean allBackToNormal = true;
        StringBuilder content = new StringBuilder();
        for (Service service : services) {
            if (service.getPushType() == MessagePushType.FORCE_PUSH) {
                content.append(adjustPushMessage(service));
                if (!service.getConnectFlag()) {
                    allBackToNormal = false;
                }
                regularPush = true;
            }
        }
        if (regularPush) {
            for (Service service : services) {
                if (service.getPushType() == MessagePushType.REGULAR_PUSH) {
                    content.append(adjustPushMessage(service));
                    if (!service.getConnectFlag()) {
                        allBackToNormal = false;
                    }
                }
            }
        }

        if (!StringUtils.isEmpty(content.toString()) && allBackToNormal) {
            content.append("该环境全部服务恢复正常!\n");
        }
        sendMessage(content.toString());
    }

    public static void sendMessage(String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }

        if (PropertyUtils.isDebug()) {
            log.info("debug mode no need push message: {}", content);
            return;
        }

        WxMessage wxMessage = new WxMessage(content);
        HttpUtils.postJsonRequest(PropertyUtils.getWxPostUrl(), String.class, wxMessage);
    }

    private static String adjustPushMessage(Service service) {
        if (service.getConnectFlag()) {
            return String.format("%s: %s\n", service.getServiceId(), service.getConnectResult());
        }

        return String.format("%s: %s[连续异常%d次]\n",
                             service.getServiceId(),
                             adjustErrorMessage(service.getConnectResult()),
                             service.getErrorCount());
    }

    private static String adjustErrorMessage(String error) {
        if (error.contains("ConnectException: Connection timed out")) {
            return "连接超时";
        }

        if (error.contains("Exception: ")) {
            String[] infos = error.split("Exception: ");
            return infos[infos.length - 1];
        }
        return error;
    }
}
