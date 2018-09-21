package com.integration.provider.mapper;

import com.integration.provider.domain.User;

/**
 * @author 蒋文龙(Vin)
 * @date 2018/9/20
 */

public interface UserMapper {
    User getOne(long userId);
}
