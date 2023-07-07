package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetBizFormListReq {

    @ApiModelProperty("组织id")
    private Long orgId;

    @ApiModelProperty("应用id")
    private List<Long> appIds;
}
