package com.jxstjh.test.demo.vx;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuge
 * @date 2021-6-16 9:33
 */
@Data
public class Article implements Serializable {

    private static final long serialVersionUID = 228150339972978127L;

    /**
     * 图文消息描述
     */
    private String Description;

    /**
     * 图片链接，支持JPG、PNG格式，<br>
     * 较好的效果为大图640*320，小图80*80
     */
    private String PicUrl;

    /**
     * 图文消息名称
     */
    private String Title;

    /**
     * 点击图文消息跳转链接
     */
    private String Url;

}