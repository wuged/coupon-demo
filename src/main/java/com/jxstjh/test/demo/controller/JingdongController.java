package com.jxstjh.test.demo.controller;

import com.jxstjh.test.demo.service.JdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuge
 * @date 2021-6-17 9:52
 */
@Slf4j
@RestController
@RequestMapping("/jd")
public class JingdongController {

    @Autowired
    private JdService jdService;

    @GetMapping
    public String get(String q) {
        return jdService.queryCoupon(q);
    }
}
