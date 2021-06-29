package com.jxstjh.test.demo.service.impl;

import com.jxstjh.test.demo.service.MessageService;
import com.jxstjh.test.demo.service.TbService;
import com.jxstjh.test.demo.util.MessageDecrypt;
import com.jxstjh.test.demo.util.MessageUtil;
import com.jxstjh.test.demo.vx.VxTextMassage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @author wuge
 * @date 2021-6-15 17:26
 */
@Slf4j
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    /**
     * 注入淘宝服务
     */
    @Autowired
    TbService tbService;

    @Override
    public String newMessageRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            String decrypt = MessageDecrypt.decrypt(request);
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(decrypt);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 消息内容
            String content = requestMap.get("Content");
            log.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType + ", content is:" + content);
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑
                /*if(content.equals("xxx")){
                }*/
                //自动回复
                VxTextMassage text = new VxTextMassage();
                text.setContent(dealContent(content));
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(new Date().getTime());
                text.setMsgType(msgType);
                respMessage = MessageUtil.textMessageToXml(text);
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //文本消息
                    VxTextMassage text = new VxTextMassage();
                    text.setContent(genMsg());
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(new Date().getTime());
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }
                // 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅

                }
            }
        }
        catch (Exception e) {
            log.error("error......");
        }
        return respMessage;
    }

    /**
     * 处理查询逻辑
     * @param content
     * @return
     */
    private String dealContent(String content) {
        if (content.contains("代码")) {
            return getCodeMsg();
        }
        try {
            String couponMsg = tbService.queryCoupon(content);
            if (couponMsg == null || couponMsg.length() == 0 ) {
                return "暂时找不到该商品的优惠卷哦，可能该商品暂无优惠";
            }
            return couponMsg;
        } catch (Exception e) {
            log.info(e.getMessage());
            return "暂时找不到该商品的优惠卷哦，可能该商品暂无优惠";
        }
    }

    /**
     * 关注自动回复
     * @return
     */
    private String genMsg() {
        StringBuffer sb = new StringBuffer();
        sb.append("欢迎关注正好想买\n");
        sb.append("搜罗全网优惠，省钱即赚钱 \n\n");
        sb.append("如何查卷：\n");
        sb.append("1.复制商品链接\n");
        sb.append("2.粘贴到公众号\n");
        sb.append("3.将返回的优惠卷信息复制打开陶宝即可，京dong直接链接跳转购买\n\n");
        sb.append("如何反俐：\n");
        sb.append("1.确认收货后，添加客服获取反俐虹包\n");
        sb.append("客服vx号：zhxmfq");
        return sb.toString();
    }

    /**
     * 领取百度网盘代码
     * @return
     */
    private String getCodeMsg() {
        StringBuffer sb = new StringBuffer();
        sb.append("百度网盘提取\n");
        sb.append("链接：https://pan.baidu.com/s/1AgpbNbnXyLKrEkhdsdrIYw \n");
        sb.append("提取码：cpxv \n");
        return sb.toString();
    }
}
