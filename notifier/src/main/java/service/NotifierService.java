package service;

import model.Constant;
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
public class NotifierService {
    public void sendMessage(List<ServiceNotifier> serviceNotifiers) {
        boolean regularPush = false;
        long minutesOfHour = System.currentTimeMillis() / 1000 / 60 % 60;
        if (minutesOfHour < Constant.REGULAR_PUSH_MINUTES_OF_HOUR) {
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

        if (StringUtils.isEmpty(content.toString())) {
            return;
        }
        WxMessage wxMessage = new WxMessage(content.toString());
        ConnectService connectService = new ConnectService();
        connectService.postJsonRequest("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=01a4d06b-9f42-4104-a125-9be13b46fbd3", String.class, wxMessage);
    }

    private String adjustPushMessage(ServiceNotifier serviceNotifier) {
        if (serviceNotifier.getConnectFlag()) {
            return String.format("%s: %s\n", serviceNotifier.getServiceId(), serviceNotifier.getConnectResult());
        }

        return String.format("%s: %s[连续异常%d次]\n",
                             serviceNotifier.getServiceId(),
                             adjustErrorMessage(serviceNotifier.getConnectResult()),
                             serviceNotifier.getErrorCount());
    }

    private String adjustErrorMessage(String error) {
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
