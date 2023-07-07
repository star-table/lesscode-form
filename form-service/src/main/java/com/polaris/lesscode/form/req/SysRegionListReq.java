package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: Liu.B.J
 * @date: 2021/1/20 18:08
 * @description: 省/市/区 请求结构体
 */
@Data
@ApiModel(value = "省/市/区 请求数据结构体", description = "省/市/区 请求数据结构体")
public class SysRegionListReq {

    @ApiModelProperty("区域等级(1:省 2:市 3:区)")
    @NotNull(message = "区域等级不能为空")
    private Integer level;

    @ApiModelProperty("上级区域id")
    private Integer parentId;

}
