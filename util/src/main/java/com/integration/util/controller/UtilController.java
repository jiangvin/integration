package com.integration.util.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/11/12
 */

@RestController
@RequestMapping(value = "/util")
public class UtilController {
    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public String mainMethod(@RequestParam(value = "name", defaultValue = "Vin") String name) {
        return String.format("hello, %s in util.", name);
    }
}
