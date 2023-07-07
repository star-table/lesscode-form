package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用数据移动请求结构体
 *
 * @author Nico
 * @date 2021/3/12 18:02
 */
@Data
public class AppValueMovingReq {

    @ApiModelProperty("要移动的数据id")
    private Long dataId;

    @ApiModelProperty("前面的数据id")
    private Long beforeId;

    @ApiModelProperty("后面的数据id")
    private Long afterId;

    @ApiModelProperty("如果为true，则认为当前排序规则为升序，默认为true")
    private boolean asc = true;

    @ApiModelProperty("子表的key")
    private String subformKey;

}
