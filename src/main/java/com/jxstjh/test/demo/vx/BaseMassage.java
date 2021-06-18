package com.jxstjh.test.demo.vx;

import lombok.Data;

/**
 * @author wuge
 * @date 2021-6-15 17:22
 */
@Data
public class BaseMassage {
    // 开发者微信号
    private String ToUserName;
    // 发送方帐号（一个OpenID）
    private String FromUserName;
    // 消息创建时间 （整型）
    private long CreateTime;
    // 消息类型（text/image/location/link）
    private String MsgType;
    // 消息id，64位整型
    private long MsgId;
    /**
     * 位0x0001被标志时，星标刚收到的消息
     */
    private int FuncFlag;
}
