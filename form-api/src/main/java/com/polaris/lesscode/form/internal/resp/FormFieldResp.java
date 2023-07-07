package com.polaris.lesscode.form.internal.resp;

import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import com.polaris.lesscode.form.internal.sula.AsyncData;
import com.polaris.lesscode.form.internal.sula.DataRely;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author: Liu.B.J
 * @data: 2020/9/25 19:57
 * @modified:
 */
@Data
@ApiModel(value="表单字段响应信息(内部调用)", description="表单字段响应信息(内部调用)")
public class FormFieldResp {

    private String name;

    private String label;

    private String formFieldType;

    private List<FormFieldResp> subFormField;
    
    /**
     * .数据库联动
     */
    private DataRely dataRely;
    
    /**
     * .动态数据关联
     */
    private AsyncData asyncData;

    /**
     * 脱敏策略
     */
    private String sensitiveStrategy;

    /**
     * 是否脱敏
     */
    private Integer sensitiveFlag = YesOrNoEnum.NO.getCode();

}
