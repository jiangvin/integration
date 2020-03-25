package model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@Data
public class Markdown {

    @NonNull
    private String content;
}
