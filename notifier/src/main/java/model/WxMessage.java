package model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@Data
public class WxMessage {

    private String msgtype = "markdown";

    @NonNull
    private Markdown markdown;

    public WxMessage(String content) {
        this.markdown = new Markdown(content);
    }
}
