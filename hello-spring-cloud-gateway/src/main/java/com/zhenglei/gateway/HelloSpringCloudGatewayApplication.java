package com.zhenglei.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class HelloSpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringCloudGatewayApplication.class, args);
    }

}
