package com.polaris.lesscode.form.resp;

import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

import com.polaris.lesscode.form.internal.sula.AsyncData;
import com.polaris.lesscode.form.internal.sula.DataRely;

/**
 * @author: Liu.B.J
 * @data: 2020/9/25 19:57
 * @modified:
 */
@Data
@ApiModel(value="表单字段详细信息返回结构体", description="表单字段详细信息返回结构体")
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

    public FormFieldResp(String name, String label, String formFieldType){
        this.name = name;
        this.label = label;
        this.formFieldType = formFieldType;
    }

    public FormFieldResp(){}
    

}
