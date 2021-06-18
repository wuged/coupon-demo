package com.jxstjh.test.demo.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jxstjh.test.demo.service.TbService;
import com.jxstjh.test.demo.util.UrlAnalyzeUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkTpwdCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
     * 通过“taobao.tbk.item.info.get( 淘宝客-公用-淘宝客商品详情查询(简版) ) ”得到“seller_id”、“title”
     * 作为taobao.tbk.dg.material.optional( 淘宝客-推广者-物料搜索 )“seller_ids”、“q”的入参，得到“coupon_share_url”或“url”
     * 作为taobao.tbk.tpwd.create( 淘宝客-公用-淘口令生成 )“url”的入参
     * @param word
     * @return
     * @throws ApiException
     */
    @Override
    public String queryCoupon(String word) throws ApiException {
        // 根据淘宝链接获取商品id
        String itemId = getItemId(word);
        //构建系统参数
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appsecret);
        TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
        req.setNumIids(itemId);
        req.setPlatform(2L);
        TbkItemInfoGetResponse rsp = client.execute(req);
        log.info(rsp.getBody());
        return "";
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
