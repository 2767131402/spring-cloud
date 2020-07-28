package com.funtl.hello.spring.cloud.web.admin.feign.service.hystrix;

import com.funtl.hello.spring.cloud.web.admin.feign.service.AdminService;
import com.funtl.hello.spring.cloud.web.admin.feign.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHystrix implements UserService {

    @Override
    public String test(String message) {
        return "Hi，your message is :" + message + " but request error.";
    }
}