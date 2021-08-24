package com.zhs.pdf2image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhs
 * @date: 2020/4/8 15:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBO {
    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 页码
     */
    private Integer page;
}
