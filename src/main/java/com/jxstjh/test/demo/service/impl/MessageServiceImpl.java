package com.jxstjh.test.demo.service.impl;

import com.jxstjh.test.demo.service.MessageService;
import com.jxstjh.test.demo.util.MessageDecrypt;
import com.jxstjh.test.demo.util.MessageUtil;
import com.jxstjh.test.demo.vx.VxTextMassage;
import lombok.extern.slf4j.Slf4j;
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
                text.setContent("霍霍哈嘿"+content);
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
                    text.setContent("谢谢你这么优秀还关注我！！");
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
}
