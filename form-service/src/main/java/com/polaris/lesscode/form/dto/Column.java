package com.polaris.lesscode.form.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * column
 *
 * @author ethanliao
 * @date 2020/12/30
 * @since 1.0.0
 */
@Data
@ApiModel("列")
public class Column {

    @ApiModelProperty("字段所在列序号")
    private Integer col;

    private Integer colspan = 1;
    private Integer rowspan = 1;

    @ApiModelProperty("字段描述")
    private String text;

    @ApiModelProperty("字段key")
    private String field;

    @ApiModelProperty("字段类型")
    private String type;

    @ApiModelProperty("是否导入，true：导入，false：不导入")
    private boolean imported = true;
}
