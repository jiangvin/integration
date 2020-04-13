package model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@Data
public class WxMessage {

    private String msgtype = "text";

    @NonNull
    private WxText text;

    public WxMessage(String content, List<String> mobileList) {
        this.text = new WxText(content);
        if (mobileList != null && !mobileList.isEmpty()) {
            this.text.setMentioned_mobile_list(mobileList);
        }
    }
}
