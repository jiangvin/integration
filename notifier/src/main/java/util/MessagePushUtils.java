package util;

import model.MessagePushType;
import model.ServiceNotifier;
import model.WxMessage;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */
public class MessagePushUtils {
    private MessagePushUtils() {

    }

    public static void sendMessage(List<ServiceNotifier> serviceNotifiers) {
        boolean regularPush = false;
        long minutesOfHour = TimeUtils.getMinutesOfHour();
        if (minutesOfHour < PropertyUtils.REGULAR_PUSH_MINUTES_OF_HOUR) {
            regularPush = true;
        }

        StringBuilder content = new StringBuilder();
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            if (serviceNotifier.getPushType() == MessagePushType.FORCE_PUSH) {
                content.append(adjustPushMessage(serviceNotifier));
                regularPush = true;
            }
        }
        if (regularPush) {
            for (ServiceNotifier serviceNotifier : serviceNotifiers) {
                if (serviceNotifier.getPushType() == MessagePushType.REGULAR_PUSH) {
                    content.append(adjustPushMessage(serviceNotifier));
                }
            }
        }

        sendMessage(content.toString());
    }

    public static void sendMessage(String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        WxMessage wxMessage = new WxMessage(content);
        HttpUtils.postJsonRequest(PropertyUtils.getWxPostUrl(), String.class, wxMessage);
    }

    private static String adjustPushMessage(ServiceNotifier serviceNotifier) {
        if (serviceNotifier.getConnectFlag()) {
            return String.format("%s: %s\n", serviceNotifier.getServiceId(), serviceNotifier.getConnectResult());
        }

        return String.format("%s: %s[连续异常%d次]\n",
                             serviceNotifier.getServiceId(),
                             adjustErrorMessage(serviceNotifier.getConnectResult()),
                             serviceNotifier.getErrorCount());
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
