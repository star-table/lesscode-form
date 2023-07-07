package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value="唯一性check请求结构体", description="唯一性check请求结构体")
public class CheckUniqueReq {

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("字段key")
    private String key;

    @ApiModelProperty("数据id")
    private Long dataId;

    @ApiModelProperty("如果是子表单，则传")
    private String subformKey;

    @ApiModelProperty("要鉴定的值")
    private Object value;
}
