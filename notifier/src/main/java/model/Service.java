package model;

import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

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

    private Timestamp startTime;

    private String version;

    private String versionWithoutPatch;

    private Integer oldGenUsed;

    private Integer oldGenMax;

    private int errorCount;

    private boolean needSave = true;

    public void setConnectResult(String result, boolean flag) {
        this.connectFlag = flag;
        this.connectResult = result;
    }

    public String getVersionWithoutPatch() {
        if (version == null || version.length() <= 5) {
            return version;
        }

        if (versionWithoutPatch == null) {
            versionWithoutPatch = version.substring(0, 5);
        }
        return versionWithoutPatch;
    }
}
