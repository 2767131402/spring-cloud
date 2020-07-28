package com.zhenglei.spring.cloud.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping(value = "/user")
    public String test(String message){
        System.out.println("UserController message:"+message);
        return "true";
    }
}
