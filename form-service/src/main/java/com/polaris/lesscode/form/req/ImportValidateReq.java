package com.polaris.lesscode.form.req;

import com.polaris.lesscode.form.dto.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ImportValidateReq {

    @ApiModelProperty("导入的excel token")
    private String token;

    @ApiModelProperty("开始导入的下标")
    private int index;

    @ApiModelProperty("应用id")
    private Long appId;

}
