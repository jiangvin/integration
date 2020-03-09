package com.integration.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * @author 蒋文龙(Vin)
 * @description 微服务中心，拥有注册中心，配置中心，链路监控的功能
 * @date 2020/03/09
 */
@EnableEurekaServer
@EnableConfigServer
@EnableZipkinServer
@SpringBootApplication
public class CenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CenterApplication.class, args);
    }

}
