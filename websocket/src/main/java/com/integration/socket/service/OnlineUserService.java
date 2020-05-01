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
    private ConcurrentHashMap<String, UserBo> sessionMap = new ConcurrentHashMap<>();

    public void add(String key, String sessionId) {
        sessionMap.put(key, new UserBo(key, sessionId));
        log.info("add new session:{}({})", key, sessionMap.size());
    }

    public boolean remove(String key) {
        if (!sessionMap.containsKey(key)) {
            return false;
        }
        sessionMap.remove(key);
        log.info("remove session:{}({})", key, sessionMap.size());
        return true;
    }

    public UserBo get(String key) {
        return sessionMap.get(key);
    }

    public List<String> getUserList() {
        List<String> users = new ArrayList<>();
        sessionMap.forEach((key, value) -> users.add(key));
        return users;
    }
}
