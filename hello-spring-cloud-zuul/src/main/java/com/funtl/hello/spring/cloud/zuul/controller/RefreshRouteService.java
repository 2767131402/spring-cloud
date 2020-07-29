package com.funtl.hello.spring.cloud.zuul.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 刷新路由服务（当DB路由有变更时，应调用refreshRoute方法）
 */
@RestController
public class RefreshRouteService {

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    RouteLocator routeLocator;

    /**
     * 手动调用刷新
     * @return
     */
    @GetMapping("/refreshRoute")
    public String refreshRoute() {
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
        return "OK";
    }
}
