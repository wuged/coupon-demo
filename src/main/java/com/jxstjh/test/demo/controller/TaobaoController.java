package com.jxstjh.test.demo.controller;

import com.jxstjh.test.demo.service.TbService;
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
@RequestMapping("/tb")
public class TaobaoController {

    @Autowired
    private TbService tbService;

    @GetMapping
    public String get(String q) {
        return tbService.queryCoupon(q);
    }

    /*@GetMapping("/test")
    public String test(String word) throws ApiException {
        return tbService.queryOption(word);
    }*/

    @GetMapping("/detail")
    public String queryIdFromContent(String q) {
        return tbService.queryItemDetail(q).toString();
    }
}
