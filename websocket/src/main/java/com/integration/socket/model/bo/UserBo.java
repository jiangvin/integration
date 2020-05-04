package com.integration.socket.model.bo;

import com.integration.socket.service.MessageService;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/28
 */
@Data
public class UserBo {
    @NonNull private String username;
    @NonNull private String socketSessionId;

    /**
     * 用户当前所在的房间号
     */
    private String roomId;

    final private List<String> subscribeList = new ArrayList<>();

    /**
     * 用户是否完成订阅
     * @return 如果订阅了TOPIC_PATH和QUEUE_PATH则算完成了订阅
     */
    public boolean isFinishSubscribe() {
        return subscribeList.contains(MessageService.TOPIC_PATH) && subscribeList.contains("/user" + MessageService.QUEUE_PATH);
    }
}
