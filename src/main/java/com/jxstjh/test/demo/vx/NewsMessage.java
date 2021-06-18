package com.jxstjh.test.demo.vx;

import lombok.Data;

import java.util.List;

/**
 * @author wuge
 * @date 2021-6-16 9:33
 */
@Data
public class NewsMessage {
    /**
     * 图文消息个数，限制为10条以内
     */
    private Integer ArticleCount;

    /**
     * 多条图文消息信息，默认第一个item为大图
     */
    private List<Article> Articles;
}
