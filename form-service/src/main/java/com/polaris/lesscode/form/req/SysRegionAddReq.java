package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Liu.B.J
 * @date: 2021/1/25 10:44
 * @description:
 */
@Data
@ApiModel(value = "省/市/区 添加数据结构体", description = "省/市/区 添加数据结构体")
public class SysRegionAddReq {

    @ApiModelProperty("省份id")
    private Integer provinceId;

    @ApiModelProperty("省份名称")
    private String provinceName;

    @ApiModelProperty("城市id")
    private String cityId;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("区id")
    private Integer areaId;

    @ApiModelProperty("区名称")
    private String areaName;

}
