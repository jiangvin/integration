package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/25
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class WxText {

    @NonNull
    private String content;

    private List<String> mentioned_mobile_list;
}
