package com.integration.provider.domain;

import lombok.Data;

/**
 * @author 蒋文龙(Vin)
 * @date 2018/9/20
 */

@Data
public class User {
    private long userId;
    private String username;
    private String password;
    private boolean enabled;
}
