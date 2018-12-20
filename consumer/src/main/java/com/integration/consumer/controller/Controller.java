package com.integration.consumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Vin on 2018/5/31.
 */
@RestController
@RequestMapping(value = "/")
@Slf4j
public class Controller {

    private final RestTemplate restTemplate;

    @Autowired
    public Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/*" , method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "fallback")
    public String mainMethod(HttpServletRequest request) {
        String uriWithQuery = request.getRequestURI();
        if (!StringUtils.isEmpty(request.getQueryString())) {
            uriWithQuery += "?" + request.getQueryString();
        }
        log.debug("Get uri with queryString:" + uriWithQuery);
        return restTemplate.getForEntity("http://provider-service" + uriWithQuery, String.class).getBody();
    }

    public String fallback(HttpServletRequest request, Throwable throwable) {
        return request.getRequestURL() + " wrong:" + throwable;
    }
}
