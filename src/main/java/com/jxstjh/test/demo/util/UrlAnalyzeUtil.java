package com.jxstjh.test.demo.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * url分析工具
 *
 * @author wuge
 * @date 2021-6-18 15:10
 */
@Slf4j
public class UrlAnalyzeUtil {

    /**
     * 第一步
     * 获取重定向的url（不是最终的）
     * @param url 原始url：类似https://m.tb.cn/h.4Et6Ogp?sm=38bb35
     * @return
     */
    public static String getRedirecUrl(String url) {
        try {
            StringBuilder buffer = new StringBuilder();
            //发送get请求
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            //必须设置false，否则会自动redirect到重定向后的地址
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
            conn.addRequestProperty("Referer", "http://matols.com/");
            conn.connect();
            //判定是否会进行302重定向
            if (conn.getResponseCode() == 302) {
                //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
                String location = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");
                serverUrl = new URL(location);
                conn = (HttpURLConnection) serverUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Charset", "UTF-8;");
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
                conn.addRequestProperty("Referer", "http://matols.com/");
                conn.connect();
                System.out.println("跳转地址:" + location);
            }
            //将返回的输入流转换成字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            // 处理得到的页面字符串，取得里面的url
            return getFinalUrl(dealHtml(buffer.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取最终的url
     * @param url
     * @return
     */
    public static String getFinalUrl(String url) {
        if (url.length() == 0) {
            return "";
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conn != null) {
            return conn.getHeaderField("Location");
        }
        return "";
    }

    /**
     * 处理得到的页面字符串，取得里面的url
     * @param htmlContent
     * @return
     */
    private static String dealHtml(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        int i = htmlContent.indexOf("var url = '");
        ArrayList<String> strings = Lists.newArrayList(Splitter.on("'").omitEmptyStrings().split(htmlContent.substring(i)));
        return strings.get(1);
    }

    /**
     * 根据最终url获取id，url必须含有id参数
     * @param url
     * @return
     */
    public static String getIdByUrl(String url) {
        int i = url.indexOf("?id=");
        if (i >= 0) {
            url.substring(i);
            ArrayList<String> strings = Lists.newArrayList(Splitter.on("&").omitEmptyStrings().split(url.substring(i)));
            return strings.get(0).replace("?id=", "");
        }
        int j = url.indexOf("&id=");
        if (j >= 0) {
            url.substring(j);
            ArrayList<String> strings = Lists.newArrayList(Splitter.on("&").omitEmptyStrings().split(url.substring(j)));
            return strings.get(0).replace("&id=", "");
        }
        return "";
    }

    /**
     * 根据最初始的url获取商品id
     * @param orginalUrl
     * @return
     */
    public static String getIdByOriginalUrl(String orginalUrl) {
        return getIdByUrl(getRedirecUrl(orginalUrl));
    }

    /*public static void main(String[] args) {
        // 获取id
        System.out.println(getIdByUrl(getRedirecUrl("https://m.tb.cn/h.4Et6Ogp?sm=38bb35")));
    }*/

}
