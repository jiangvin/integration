package com.integration.provider.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author 蒋文龙(Vin)
 * @className Talk
 * @description
 * @date 2019/6/13
 */

@Data
@NoArgsConstructor
public class Talk {
    @NonNull
    private String id;
    @NonNull
    private Integer value;
}
