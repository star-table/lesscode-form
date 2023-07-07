package com.polaris.lesscode.form.req;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportDataReq {

    @ApiModelProperty("导入的excel token")
    private String token;

    @ApiModelProperty("要导入的应用id")
    private Long appId;

    @ApiModelProperty("开始导入的下标")
    private int index;

    @ApiModelProperty("导入类型，1：仅新增，2：仅编辑，3：新增和编辑")
    private int type;

}
