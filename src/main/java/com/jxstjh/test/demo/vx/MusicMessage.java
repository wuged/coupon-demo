package com.jxstjh.test.demo.vx;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuge
 * @date 2021-6-16 9:35
 */
@Data
public class MusicMessage extends BaseMassage implements Serializable {

    /**
     * 音乐
     */
    private Music Music;

}
