package com.jxstjh.test.demo.service;

import com.taobao.api.ApiException;

/**
 * @author wuge
 * @date 2021-6-17 9:54
 */
public interface TbService {

    /**
     * 查询优惠
     * @param word
     */
    String queryCoupon(String word) throws ApiException;

}
