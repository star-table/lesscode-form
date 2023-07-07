package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2021/1/7 15:52
 * @description:
 */
@Data
@ApiModel(value="移动表格数据请求结构体", description="移动表格数据请求结构体")
public class AppValueMoveReq {

    @NotEmpty(message = "移动参数不能为空")
    private List<MoveParameter> moves;

}
