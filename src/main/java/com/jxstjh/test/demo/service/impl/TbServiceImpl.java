package com.jxstjh.test.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jxstjh.test.demo.service.JdService;
import com.jxstjh.test.demo.service.TbService;
import com.jxstjh.test.demo.util.UrlAnalyzeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author wuge
 * @date 2021-6-17 9:55
 */
@Slf4j
@Service
public class TbServiceImpl implements TbService {

    /**
     * 淘宝appkey
     */
    @Value("${tb.app.key}")
    private String appkey;

    /**
     * 淘宝appsecret
     */
    @Value("${tb.app.secret}")
    private String appsecret;

    /**
     * 淘宝appsecret
     */
    @Value("${tb.app.adzoneid}")
    private Long adzoneid;

    /**
     * 淘宝接口
     */
    @Value("${tb.api.url}")
    private String url;

    /**
     * 第三方接口
     */
    @Value("${third.api.url}")
    private String thirdApiUrl;

    /**
     * 第三方接口密钥
     */
    @Value("${third.apikey}")
    private String thirdApiKey;

    /**
     * 推广位id
     */
    @Value("${tb.app.pid}")
    private String pid;

    /**
     * 第三方接口密钥
     */
    @Value("${third.sid}")
    private String sid;

    /**
     * 注入restTemplate
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 注入JdService
     */
    @Autowired
    private JdService jdService;

    @Override
    public String queryCoupon(String word) {
        if (word.indexOf(".jd.com") > -1) {
            // 京东
            return jdService.queryCoupon(word);
        }
        // 淘宝
        return queryTbConpon(word);
    }

    /**
     * 查询淘宝优惠
     * @param word
     * @return
     */
    private String queryTbConpon(String word) {
        JSONObject result = queryItemDetail(word);
        if (result == null) {
            return "";
        }
        String title = result.getString("tao_title");
        String zk_final_price = result.getString("size");
        String finalPrice = result.getString("quanhou_jiage");
        String coupon_amount = result.getString("coupon_info_money");
        String password_simple = result.getString("tkl");
        String tkfee3 = result.getString("tkfee3");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(title).append("\n")
                .append("【现价】：").append(zk_final_price).append("\n");
        if (!"0".equals(coupon_amount)) {
            stringBuilder.append("【优惠金额】：").append(coupon_amount).append("\n")
                    .append("【券后价】：").append(finalPrice).append("\n");
        }
        if (StringUtils.isNotBlank(tkfee3)) {
            BigDecimal multiply = new BigDecimal(tkfee3).multiply(new BigDecimal("0.6"));
            stringBuilder.append("【返俐】：").append(multiply.toString()).append("\n");
        }
        stringBuilder.append(password_simple).append("\n")
                .append("——————————\n")
                .append("【购买方法】：\n")
                .append("1.长按选择一键复制\n")
                .append("2.打开手机桃宝\n")
                .append("——————————\n");
        return stringBuilder.toString();
    }

    /**
     * 旧版逻辑
     * 通过“taobao.tbk.item.info.get( 淘宝客-公用-淘宝客商品详情查询(简版) ) ”得到“seller_id”、“title”
     * 作为taobao.tbk.dg.material.optional( 淘宝客-推广者-物料搜索 )“seller_ids”、“q”的入参，得到“coupon_share_url”或“url”
     * 作为taobao.tbk.tpwd.create( 淘宝客-公用-淘口令生成 )“url”的入参
     * @param word
     * @return
     * @throws ApiException
     */
    /*@Override
    public String queryCoupon(String word) throws ApiException {
        // 根据淘宝链接获取商品id
        // 根据消息分类
        // 第三方查询
        if (word.indexOf(".jd.com") > -1) {
            // 京东
            return jdService.queryCoupon(word);
        }
        //String itemId = queryItemId(word);
        String itemId = "1";
        if (StringUtils.isBlank(itemId)) {
            if (word.indexOf("https://m.tb.cn") > -1) {
                // 本地查询：通过解析url
                itemId = getItemId(word);
            } else {
                return "";
            }
        }
        //构建系统参数
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appsecret);
        TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
        req.setNumIids(itemId);
        req.setPlatform(2L);
        TbkItemInfoGetResponse rsp = client.execute(req);
        if (!rsp.isSuccess()) {
            return "";
        }
        //log.info("商品详情：" + rsp.getBody());
        JSONObject jsonObject = JSONObject.parseObject(rsp.getBody());
        JSONObject item = jsonObject.getJSONObject("tbk_item_info_get_response").getJSONObject("results").getJSONArray("n_tbk_item").getJSONObject(0);
        String seller_id = item.getString("seller_id");
        String title = item.getString("title");
        TbkDgMaterialOptionalRequest optionalRequest = new TbkDgMaterialOptionalRequest();
        optionalRequest.setSellerIds(seller_id);
        optionalRequest.setQ(title);
        optionalRequest.setAdzoneId(adzoneid);
        TbkDgMaterialOptionalResponse optionalResponse = client.execute(optionalRequest);
        if (!optionalResponse.isSuccess()) {
            return "";
        }
        log.info("物料详情：" + optionalResponse.getBody());
        JSONObject optionalJsonObject = JSONObject.parseObject(optionalResponse.getBody());
        JSONObject optionalItem = optionalJsonObject.getJSONObject("tbk_dg_material_optional_response").getJSONObject("result_list").getJSONArray("map_data").getJSONObject(0);
        String coupon_share_url = optionalItem.getString("coupon_share_url");
        String url = optionalItem.getString("url");
        // 现价
        String zk_final_price = optionalItem.getString("zk_final_price");
        // 优惠券
        String coupon_amount = optionalItem.getString("coupon_amount");
        if (coupon_amount == null) {
            coupon_amount = "0";
        }
        // 券后价
        String finalPrice = countFinalPrice(zk_final_price, coupon_amount);
        // 如果有优惠券就用优惠券
        if (coupon_share_url != null) {
            url = coupon_share_url;
        }
        TbkTpwdCreateRequest createRequest = new TbkTpwdCreateRequest();
        createRequest.setUrl("https:" + url);
        TbkTpwdCreateResponse createResponse = client.execute(createRequest);
        if (!createResponse.isSuccess()) {
            return "";
        }
        // log.info("淘口令详情：" + optionalResponse.getBody());
        JSONObject createJsonObject = JSONObject.parseObject(createResponse.getBody());
        JSONObject createItem = createJsonObject.getJSONObject("tbk_tpwd_create_response").getJSONObject("data");
        String password_simple = createItem.getString("password_simple");
        // String model = createItem.getString("model");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(title).append("\n")
                .append("【现价】：").append(zk_final_price).append("\n");
        if (!"0".equals(coupon_amount)) {
            stringBuilder.append("【优惠金额】：").append(coupon_amount).append("\n")
                    .append("【券后价】：").append(finalPrice).append("\n");
        }
        stringBuilder.append(password_simple).append("\n")
                .append("——————————\n")
                .append("【购买方法】：\n")
                .append("1.长按选择一键复制\n")
                .append("2.打开手机桃宝\n")
                .append("——————————\n");
        return stringBuilder.toString();
    }*/

    /*@Override
    public String queryOption(String word) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appsecret);
        TbkDgMaterialOptionalRequest optionalRequest = new TbkDgMaterialOptionalRequest();
        optionalRequest.setQ(word);
        optionalRequest.setAdzoneId(adzoneid);
        TbkDgMaterialOptionalResponse optionalResponse = client.execute(optionalRequest);
        if (!optionalResponse.isSuccess()) {
            return "";
        }
        log.info("物料详情：" + optionalResponse.getBody());
        return optionalResponse.getBody();
    }*/

    @Override
    public JSONObject queryItemDetail(String content) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appkey", thirdApiKey);
        params.put("sid", sid);
        params.put("tkl", content);
        params.put("signurl", 5);
        params.put("pid", pid);
        String result;
        try {
            result = restTemplate.getForObject(thirdApiUrl  + "?appkey={appkey}&sid={sid}&tkl={tkl}&signurl={signurl}&pid={pid}", String.class, params);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer code = jsonObject.getInteger("status");
            if (code == 200) {
                return jsonObject.getJSONArray("content").getJSONObject(0);
            } else {
                log.info("第三方接口报错：" + jsonObject.getString("content"));
                return null;
            }
        } catch (Exception e) {
            log.info("第三方接口调用异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 计算券后价
     * @param origin
     * @param coupon
     * @return
     */
    private String countFinalPrice(String origin, String coupon) {
        if (coupon == null) {
            return origin;
        }
        return new BigDecimal(origin).subtract(new BigDecimal(coupon)).toString();
    }

    /**
     * 根据淘宝链接获取商品id
     * 淘口令类似：1.0啊8EIjXhqDS3q信 https://m.tb.cn/h.4Et6Ogp?sm=38bb35  罗技G302有线电竞游戏鼠标绝地求生/LOL/吃鸡鼠标宏台式电脑男生女生电脑外设 g302
     * @param word
     * @return
     */
    private String getItemId(String word) {
        ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(word));
        if (strings.size() < 2) {
            return "";
        }
        return UrlAnalyzeUtil.getIdByOriginalUrl(strings.get(1));
    }
}
