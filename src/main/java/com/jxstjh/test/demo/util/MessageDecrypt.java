package com.jxstjh.test.demo.util;

import com.jxstjh.test.demo.exception.AesException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuge
 * @date 2021-6-16 9:20
 */
@Component
public class MessageDecrypt {
    /**
     * 公众平台上，开发者设置的EncodingAESKey
     */
    @Value("${vx.encodingAesKey}")
    private String encodingAesKey;
    private static String encodingAesKeyValue;
    /**
     * 公众平台上，开发者设置的token
     */
    @Value("${vx.token}")
    private String token;
    private static String tokenValue;
    /**
     * 公众平台appid
     */
    @Value("${vx.appId}")
    private String appId;
    private static String appIdValue;

    public static String decrypt(HttpServletRequest request) throws AesException, IOException {
        //获取请求中的时间戳和随机数,微信加密签名
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");

        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();

        InputStream ins = null;
        try {
            ins = request.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Document doc = null;
        try {
            doc = reader.read(ins);
            org.dom4j.Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }finally{
            ins.close();
        }

        WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(tokenValue, encodingAesKeyValue, appIdValue);
        String s = wxBizMsgCrypt.decryptMsg(signature, timestamp, nonce, map.get("Encrypt"));
        return s;
    }

    /**
     * 利用@PostConstruct将application中配置的值赋给本地的变量
     */
    @PostConstruct
    public void getServelPort(){
        tokenValue = this.token;
        appIdValue = this.appId;
        encodingAesKeyValue = this.encodingAesKey;
    }
}
