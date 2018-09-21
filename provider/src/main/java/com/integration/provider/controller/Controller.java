package com.integration.provider.controller;

import com.integration.provider.domain.User;
import com.integration.provider.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Vin on 2018/5/31.
 */
@RestController
@RequestMapping(value = "/")
public class Controller {
    private final UserMapper userMapper;

    @Autowired
    public Controller(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public String mainMethod(@RequestParam(value = "name", defaultValue = "Vin") String name) {
        return name + ",Welcome to my provider !";
    }

    @GetMapping(value = "/mybatis")
    public String myBatisDemo(@RequestParam(value = "userId",defaultValue = "1") long userId) {
        User u = userMapper.getOne(userId);
        return u.getUsername() + ",Welcome to mybatis";
    }
}
