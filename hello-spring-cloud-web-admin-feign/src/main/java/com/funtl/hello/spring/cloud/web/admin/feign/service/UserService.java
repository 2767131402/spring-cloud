package com.funtl.hello.spring.cloud.web.admin.feign.service;

import com.funtl.hello.spring.cloud.web.admin.feign.service.hystrix.UserServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "hello-spring-cloud-service",fallback = UserServiceHystrix.class)
public interface UserService {

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public String test(@RequestParam(value = "message") String message);
}
