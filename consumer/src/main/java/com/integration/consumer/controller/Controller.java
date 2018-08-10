package com.integration.consumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Vin on 2018/5/31.
 */
@RestController
@RequestMapping(value = "/")
public class Controller {

    enum Type {
        MEMBER
    }

    private final RestTemplate restTemplate;

    @Autowired
    public Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "fallback")
    public String mainMethod(@RequestParam(value = "name", defaultValue = "Consumer") String name) {
        return restTemplate.getForEntity("http://provider-service?name=" + name, String.class).getBody();

    }

    public String fallback(String name) {
        return name + " wrong !";
    }
}
