package com.integration.provider.controller;

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
    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public String mainMethod(@RequestParam(value = "name", defaultValue = "Vin") String name) {
        return name + ",Welcome to my provider !";
    }
}
