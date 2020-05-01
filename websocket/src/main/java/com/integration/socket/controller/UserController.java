package com.integration.socket.controller;

import com.integration.socket.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/5/1
 */

@RestController("user")
@Slf4j
public class UserController {

    private final OnlineUserService onlineUserService;

    public UserController(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @GetMapping("/getAll")
    public List<String> getUsers() {
        return onlineUserService.getUserList();
    }
}
