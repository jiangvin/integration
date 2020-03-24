package model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/24
 */

@Data
public class ServiceNotifier {

    @NonNull
    private String serviceId;

    @NonNull
    private String url;

    private Boolean connectFlag;

    private String connectResult;

    private int errorCount;

    public void setConnectResult(String result, boolean flag) {
        this.connectFlag = flag;
        this.connectResult = result;
    }
}
