package util;

import lombok.extern.slf4j.Slf4j;
import model.MessagePushProperty;
import model.Service;
import model.WxMessage;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

        sendMessage(messagePushProperty.generatePushContent(services.size()), messagePushProperty.getMentionedList());
    }

    public static void sendMessage(String content) {
        sendMessage(content, null);
    }

    private static void sendMessage(String content, List<String> mentionedList) {
        if (StringUtils.isEmpty(content)) {
            return;
        }

        //去掉最后一个换行符号
        if (content.lastIndexOf("\n") == content.length() - 1) {
            content = content.substring(0, content.length() - 1);
        }

        //优化提示信息，避免太多
        String[] infos = content.split("\n");
        if (infos.length > 5) {
            List<String> keepInfos = new ArrayList<>();
            for (int i = 0; i < infos.length; ++i) {
                String info = infos[i];
                if (!info.contains("恢复正常") || i == infos.length - 1) {
                    keepInfos.add(info);
                }
            }

            //缩减后依然大于5
            if (keepInfos.size() > 5) {
                keepInfos = keepInfos.subList(0, 5);
            }

            StringBuilder contentBuilder = new StringBuilder();
            for (String keepInfo : keepInfos) {
                contentBuilder.append(keepInfo).append("\n");
            }
            contentBuilder.append(String.format("(等总共%d条信息)", infos.length));
            content = contentBuilder.toString();
        }


        WxMessage wxMessage = new WxMessage(content, mentionedList);
        if (PropertyUtils.isDebug()) {
            log.info("debug mode no need push message: {}", wxMessage.toString());
            return;
        }

        HttpUtils.postJsonRequest(PropertyUtils.getWxPostUrl(), String.class, wxMessage);
    }
}
