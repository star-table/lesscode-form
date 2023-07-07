package com.polaris.lesscode.form.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExportTemplateReq {

    @ApiModelProperty("要导出的应用id")
    private Long appId;

}
