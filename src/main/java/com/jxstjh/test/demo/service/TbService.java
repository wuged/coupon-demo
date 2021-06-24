package com.jxstjh.test.demo.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wuge
 * @date 2021-6-17 9:54
 */
public interface TbService {

    /**
     * 查询优惠
     * @param word
     */
    String queryCoupon(String word);

    /**
     * 物料查询
     * @param word
     * @return
     */
    /*String queryOption(String word) throws ApiException;*/

    /**
     * 根据淘口令获取商品id
     * @param content
     * @return
     */
    JSONObject queryItemDetail(String content);
}
