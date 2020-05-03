package com.integration.socket.service;

import com.integration.socket.model.bo.UserBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 蒋文龙(Vin)
 * @description socket连接管理服务
 * @date 2020/4/23
 */

@Service
@Slf4j
public class OnlineUserService {
    private ConcurrentHashMap<String, UserBo> userMap = new ConcurrentHashMap<>();

    public boolean exists(String key) {
        return userMap.containsKey(key);
    }

    void add(UserBo userBo) {
        userMap.put(userBo.getUsername(), userBo);
    }

    boolean remove(String key) {
        if (!userMap.containsKey(key)) {
            return false;
        }
        userMap.remove(key);
        log.info("remove user:{} in user service(count:{})", key, userMap.size());
        return true;
    }

    UserBo get(String key) {
        return userMap.get(key);
    }

    public List<String> getUserList() {
        List<String> users = new ArrayList<>();
        userMap.forEach((key, value) -> users.add(key));
        return users;
    }
}
