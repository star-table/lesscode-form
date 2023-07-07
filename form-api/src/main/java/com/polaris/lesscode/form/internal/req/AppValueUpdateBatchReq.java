package com.polaris.lesscode.form.internal.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Set;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="批量更新表单数据请求结构体", description="批量更新表单数据请求结构体")
public class AppValueUpdateBatchReq {
    private Long orgId;

    private Long userId;

    @ApiModelProperty("条件")
    private Condition condition;

    @ApiModelProperty("更新")
    private List<Set> sets;
}
