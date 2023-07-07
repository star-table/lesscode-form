package com.polaris.lesscode.form.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: Liu.B.J
 * @date: 2021/1/11 12:00
 * @description: 字段格式校验
 */
@Data
public class LinkFormatField {

    @NotBlank
    private String name;

    @NotBlank
    private String path;

}
