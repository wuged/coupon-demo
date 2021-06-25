package com.jxstjh.test.demo.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * http工具类
 * @author wuge
 * @date 2021-6-25 14:51
 */
@Slf4j
@Component
public class RestTemplateUtil {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * get查询
     * @param url
     * @return
     */
    public JSONObject getForObject(String url) {
        String result;
        try {
            result = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result);
            return jsonObject;
        } catch (Exception e) {
            log.info("第三方京东接口调用异常：" + e.getMessage());
            return null;
        }
    }
}
