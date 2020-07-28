package com.funtl.hello.spring.cloud.web.admin.feign.controller;

import com.funtl.hello.spring.cloud.web.admin.feign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user")
    public String test(){
        return userService.test("111");
    }
}
