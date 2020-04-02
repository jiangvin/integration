package model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/24
 */

@Data
public class Service {

    @NonNull
    private String serviceId;

    @NonNull
    private String url;

    private MessagePushType pushType = MessagePushType.NO_PUSH;

    private Boolean connectFlag;

    private String connectResult;

    private int errorCount;

    private boolean needSave = true;

    public void setConnectResult(String result, boolean flag) {
        this.connectFlag = flag;
        this.connectResult = result;
    }
}
