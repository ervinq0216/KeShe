package com.waf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@RestController
public class TestController {

    // 模拟一个查询接口，攻击者可能会在这里尝试 SQL 注入
    @GetMapping("/api/user")
    public String getUser(@RequestParam String name) {
        return "查询成功: 找到用户 " + name;
    }

    // 模拟一个普通的页面
    @GetMapping("/index")
    public String index() {
        return "这里是安全的业务系统首页。";
    }

    @PostMapping("/api/data")
    public String postData(@RequestBody Map<String, Object> data) {
        return "POST请求成功，接收到数据: " + data.toString();
    }
}