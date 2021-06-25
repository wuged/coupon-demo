package com.jxstjh.test.demo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxstjh.test.demo.service.JdService;
import com.jxstjh.test.demo.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author wuge
 * @date 2021-6-22 15:24
 */
@Slf4j
@Service
public class JdServiceImpl implements JdService {

    /**
     * 商品详情接口
     */
    @Value("${third.api.jd.item.detail.url}")
    private String thirdApiJdItemDetailUrl;

    /**
     * 商品详情（含优惠券）接口
     */
    @Value("${third.api.jd.item.detail1.url}")
    private String thirdApiJdItemDetail1Url;

    /**
     * 商品转链接口
     */
    @Value("${third.api.jd.change.url}")
    private String thirdApiJdChangeUrl;

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
     * 京东联盟推广位id
     */
    @Value("${jd.position.id}")
    private String positionId;

    /**
     * 注入restTemplate
     */
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public String queryCoupon(String word) {
        JSONObject jsonObject = restTemplateUtil.getForObject(thirdApiJdItemDetailUrl + "?appkey=" + thirdApiKey + "&content=" + word);
        if (jsonObject == null) {
            return "";
        }
        JSONObject item = jsonObject.getJSONObject("jd_union_open_goods_promotiongoodsinfo_query_response").getJSONObject("result").getJSONArray("data").getJSONObject(0);
        String title = item.getString("goodsName");
        String skuId = item.getString("skuId");
        // 根据商品id查询详情

        JSONObject jsonObject1 = restTemplateUtil.getForObject(thirdApiJdItemDetail1Url + "?appkey=" + thirdApiKey + "&skuIds=" + skuId);
        if (jsonObject1 == null) {
            return "";
        }
        JSONObject itemDetail = jsonObject1.getJSONObject("jd_union_open_goods_query_response").getJSONObject("result").getJSONArray("data").getJSONObject(0);
        String materialUrl = itemDetail.getString("materialUrl");
        JSONArray couponList = itemDetail.getJSONObject("couponInfo").getJSONArray("couponList");
        String queryUrl = thirdApiJdChangeUrl + "?appkey=" + thirdApiKey
                + "&materialId=" + materialUrl + "&unionId=" + unionId + "&positionId=" + positionId;
        // 如果有优惠券就拼接优惠券查询
        if (couponList != null && couponList.size() > 0) {
            String link = couponList.getJSONObject(0).getString("link");
            queryUrl += ("&couponUrl=" + link);
        }
        // 根据优惠券获取转链
        JSONObject jsonObject2 = restTemplateUtil.getForObject(queryUrl);
        if (jsonObject2 == null) {
            return "";
        }
        String shortUrl = jsonObject2.getJSONObject("jd_union_open_promotion_byunionid_get_response").getJSONObject("result").getJSONObject("data").getString("shortURL");
        return dealJdCouponInfo(itemDetail, shortUrl);
    }



    /**
     * 处理返回结果并返回优惠券信息
     * @param result
     * @return
     */
    private String dealJdCouponInfo(JSONObject result, String shortUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject priceInfo = result.getJSONObject("priceInfo");
        // 佣金信息
        JSONObject commissionInfo = result.getJSONObject("commissionInfo");
        String title = result.getString("skuName");
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
                .append("【抢购链接】：").append(shortUrl).append("\n")
                .append("——————————\n");
        return stringBuilder.toString();
    }

}
