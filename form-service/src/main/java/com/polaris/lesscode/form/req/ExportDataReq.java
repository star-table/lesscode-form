package com.polaris.lesscode.form.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExportDataReq {

    @ApiModelProperty("要导出的应用id")
    private Long appId;

    @ApiModelProperty("导出类型，1：导出所有数据，2：导出筛选后的数据")
    private Integer type;

    @ApiModelProperty("是否要导出dataId")
    private Boolean enableId;

    @ApiModelProperty("导出字段列表")
    private List<String> fields;

    @ApiModelProperty("条件")
    private Condition condition;

    @ApiModelProperty("排序")
    private List<Order> orders;
}
