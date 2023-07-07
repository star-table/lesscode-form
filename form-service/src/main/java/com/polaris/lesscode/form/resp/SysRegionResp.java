package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: Liu.B.J
 * @date: 2021/1/20 18:08
 * @description: 省/市/区返回结构体
 */
@Data
@ApiModel(value = "省/市/区返回结构体", description = "省/市/区返回结构体")
public class SysRegionResp {

    private Integer regionId;

    private String regionName;

    private Integer level;

}
