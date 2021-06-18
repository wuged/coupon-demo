package com.jxstjh.test.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuge
 * @date 2021-6-15 15:37
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/sayhi")
    public String hello() {
        return "Hi.";
    }
}
