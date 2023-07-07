package com.polaris.lesscode.form.openapi.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AddValuesReq {

    @ApiModelProperty("插入的数据")
    private List<Map<String, Object>> values;

    @ApiModelProperty("前面的数据id")
    private Long beforeId;

    @ApiModelProperty("后面的数据id")
    private Long afterId;
}
