package com.polaris.lesscode.form.internal.form;

import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import lombok.Data;

/**
 * 字段选项配置
 *
 * @author Nico
 * @date 2021/3/10 11:58
 */
@Data
public class FormFieldOptions {

    /**
     * 显示颜色
     **/
    private String color;

    /**
     * 标识
     **/
    private String id;

    /**
     * 数据内容
     **/
    private String value;

}
