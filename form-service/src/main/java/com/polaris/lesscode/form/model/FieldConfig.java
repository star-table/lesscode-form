package com.polaris.lesscode.form.model;

import lombok.Data;

/**
 * @Author Liu.B.J
 */
@Data
public class FieldConfig {

    private Integer fieldWidth;// 字段占比

    private Boolean required;// 是否必填

    //private Boolean noRepeat;// 是否允许重复值

    private Integer minWordCount;// 最少字数

    private Integer maxWordCount;// 最多字数

    private Integer dateType;// 日期类型 1：年-月-日 2：年-月-日 时:分 3：年-月-日 时:分:秒

    private Integer numberFormat;// 数字匹配类型 1：无 2：显示千位分隔符 3：显示百分比

    private Boolean canDecimal;// 允许小数

    private Integer decimalDigit;// 限制位数

    private Boolean numberLimitStatus;// 是否限制数值范围

    private Double minValue;// 最小值

    private Double maxValue;// 最大值

    private Integer currencyType;// 货币类型 1：人名币 2：美元 3：欧元 4：韩元 5：日元 6：新台币 7：印度卢比

}
