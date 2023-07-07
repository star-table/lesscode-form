package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 10:12 上午
 */
@Data
@ApiModel(value="表单字段返回结构体", description="表单字段返回结构体")
public class AppFormFieldResp {

    @ApiModelProperty
    private Integer code;

    @ApiModelProperty
    private String type;

    @ApiModelProperty
    private String name;
}
