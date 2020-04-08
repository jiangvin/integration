package util;

import lombok.extern.slf4j.Slf4j;
import model.MessagePushProperty;
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

        MessagePushProperty messagePushProperty = new MessagePushProperty();

        //强制推送
        services.forEach(messagePushProperty::processForcePush);

        //定期推送
        if (messagePushProperty.isRegularPush()) {
            services.forEach(messagePushProperty::processRegularPush);
        }

        sendMessage(messagePushProperty.generatePushContent(services.size()));
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
}
