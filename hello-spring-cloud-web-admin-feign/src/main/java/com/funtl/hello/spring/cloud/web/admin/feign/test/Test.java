package com.funtl.hello.spring.cloud.web.admin.feign.test;

import java.util.HashMap;

/**
 * @author zhenglei
 * @version 1.0.0
 * @ClassName Test.java
 * @Description TODO
 * @createTime 2019年10月30日
 */
public class Test {
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("123","123");
        map.put("123","234");

        System.err.println(map.get("1235"));
    }

//    public static void main(String[] args) {
//        for (int binCount = 0; ; ++binCount) {
//            System.err.println(binCount);
//        }
//    }
}
