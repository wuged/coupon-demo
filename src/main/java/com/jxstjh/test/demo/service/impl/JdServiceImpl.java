package com.jxstjh.test.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jxstjh.test.demo.service.JdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author wuge
 * @date 2021-6-22 15:24
 */
@Slf4j
@Service
public class JdServiceImpl implements JdService {

    /**
     * 第三方接口
     */
    @Value("${third.api.jd.url}")
    private String thirdApiJdUrl;

    /**
     * 第三方接口密钥
     */
    @Value("${third.apikey}")
    private String thirdApiKey;

    /**
     * 京东联盟id
     */
    @Value("${jd.union.id}")
    private String unionId;

    /**
     * 注入restTemplate
     */
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String queryCoupon(String word) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apikey", thirdApiKey);
        params.put("materialId", word);
        params.put("unionId", unionId);
        params.put("autoSearch", true);
        params.put("type", 1);
        String result;
        try {
            result = restTemplate.postForObject(thirdApiJdUrl, params, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer code = jsonObject.getInteger("code");
            if (code == 200) {
                return dealJdCouponInfo(jsonObject);
            } else {
                log.info("第三方京东接口报错：" + jsonObject.getString("msg"));
                return "";
            }
        } catch (Exception e) {
            log.info("第三方京东接口调用异常：" + e.getMessage());
            return "";
        }
    }

    /**
     * 处理返回结果并返回优惠券信息
     * @param jsonObject
     * @return
     */
    private String dealJdCouponInfo(JSONObject jsonObject) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject result = jsonObject.getJSONObject("data");
        JSONObject priceInfo = result.getJSONObject("priceInfo");
        // 佣金信息
        JSONObject commissionInfo = result.getJSONObject("commissionInfo");
        String title = result.getString("skuName");
        String shortURL = result.getString("shortURL");
        BigDecimal price = priceInfo.getBigDecimal("price");
        BigDecimal lowestCouponPrice = priceInfo.getBigDecimal("lowestCouponPrice");
        stringBuilder.append(title).append("\n")
                .append("【京东价】：").append(price).append("\n");
        if (price.subtract(lowestCouponPrice).intValue() > 0) {
            stringBuilder.append("【优惠金额】：").append(price.subtract(lowestCouponPrice).toString()).append("\n")
                    .append("【券后价】：").append(lowestCouponPrice).append("\n");
        }
        if (commissionInfo != null) {
            String couponCommission = commissionInfo.getString("couponCommission");
            BigDecimal multiply = new BigDecimal(couponCommission).multiply(new BigDecimal("0.6"));
            stringBuilder.append("【预计返俐】：").append(multiply.toString()).append("\n");
        }
        stringBuilder.append("——————————\n")
                .append("【抢购链接】：").append(shortURL).append("\n")
                .append("——————————\n");
        return stringBuilder.toString();
    }

}
