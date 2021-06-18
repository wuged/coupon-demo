package com.jxstjh.test.demo.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuge
 * @date 2021-6-15 17:24
 */
public interface MessageService {

    /**
     * 微信公众号处理
     * @param request
     * @return
     */
    String newMessageRequest(HttpServletRequest request);
}
