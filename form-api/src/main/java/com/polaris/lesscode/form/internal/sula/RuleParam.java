package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.exception.SystemException;
import com.polaris.lesscode.form.internal.enums.DateTypeEnum;
import com.polaris.lesscode.form.internal.enums.NumberFormatEnum;
import com.polaris.lesscode.util.DateFormatUtil;
import com.polaris.lesscode.vo.ResultCode;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Liu.B.J
 */
@Data
public class RuleParam {

    private String message;// 提示

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
