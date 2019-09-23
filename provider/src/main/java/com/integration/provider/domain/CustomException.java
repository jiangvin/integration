package com.integration.provider.domain;

import lombok.Getter;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/9/21
 */

public class CustomException extends RuntimeException {

    @Getter
    private int code;

    @Getter
    private String msg;

    public CustomException(int code, @NonNull String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
