package com.polaris.lesscode.form.internal.req;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Liu.B.J
 * @version 1.0
 * @date 2020-08-04 11:42 上午
 */
@Data
@ApiModel(value="表单设计请求信息(内部调用)", description="表单设计请求信息(内部调用)")
public class AppFormDesignSaveReq {

    private List<FieldParam> config;

    private String name;

    private Integer groupType;

    private Long appId;

}
