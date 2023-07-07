package com.polaris.lesscode.form.internal.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 表单字段
 *
 * @author Nico
 * @date 2021/3/10 11:58
 */
@Data
public class FormFieldRules {

    private String message;// 提示

    private Boolean unique; // 是否唯一

    private Boolean required;// 是否必填

    private BigDecimal min;// 最小值

    private BigDecimal max;// 最大值

    private String startTime;   //开始时间

    private String endTime;     //结束时间（时间范围）

    private Integer maxLen;     //最大长度

    private Integer minLen;     //最小长度

    private String dateFormat; //日期格式

    private String regex;   //正则表达式

}
