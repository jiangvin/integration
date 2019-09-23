package com.integration.provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/9/23
 */

@Data
public class ResultData {
    public ResultData(Object obj) {
        this.code = 1000;
        this.message = "success";
        this.data = obj;
    }

    public ResultData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
    private Object data;
}
