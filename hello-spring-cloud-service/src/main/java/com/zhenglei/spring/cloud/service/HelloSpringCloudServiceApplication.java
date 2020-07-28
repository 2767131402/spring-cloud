package com.zhenglei.spring.cloud.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class HelloSpringCloudServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringCloudServiceApplication.class, args);
    }

}
