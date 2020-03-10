package com.integration.provider.controller;

import com.integration.provider.domain.CustomException;
import com.integration.provider.domain.User;
import com.integration.util.message.MessageUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vin
 * Created by Vin on 2018/5/31.
 */
@RestController
@RequestMapping(value = "/")
public class Controller {
    @RequestMapping(value = "" , method = RequestMethod.GET)
    public String mainMethod(@RequestParam(value = "name", defaultValue = "Vin") String name) {
        if ("Jefy".equals(name)) {
            throw new CustomException(1200, MessageUtil.get("permission.denied"));
        }

        return MessageUtil.get("welcome", name, "Provider");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String mainPostMethod(@RequestBody User user) {
        return MessageUtil.get("welcome", user.getUsername(), user.getUserId());
    }

    @RequestMapping(value = "user" , method = RequestMethod.GET)
    public User mainUser(@RequestParam(value = "name", defaultValue = "Vin") String name) {
        User user = new User();
        if ("Jefy".equals(name)) {
            user = null;
        }
        user.setUserId(5257);
        user.setUsername(name);
        user.setPassword("123456");
        return user;
    }
}
