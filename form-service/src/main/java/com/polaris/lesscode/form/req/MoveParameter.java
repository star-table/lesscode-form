package com.polaris.lesscode.form.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: Liu.B.J
 * @date: 2021/1/7 15:55
 * @description:
 */
@Data
public class MoveParameter {

    @NotBlank(message = "表格数据ID不能为空")
    private String id;

    private Boolean move;

    private String nextDataId;

}
