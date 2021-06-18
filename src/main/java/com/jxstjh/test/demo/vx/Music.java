package com.jxstjh.test.demo.vx;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuge
 * @date 2021-6-16 9:34
 */
@Data
public class Music implements Serializable {
    private static final long serialVersionUID = 381822571255731959L;
    /**
     * 音乐名称
     */
    private String Title;

    /**
     * 音乐描述
     */
    private String Description;

    /**
     * 音乐链接
     */
    private String MusicUrl;

    /**
     * 高质量音乐链接，WIFI环境优先使用该链接播放音乐
     */
    private String HQMusicUrl;
}