package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportPreReq {

    @ApiModelProperty("要导入的应用id，如果不存在可不传")
    private Long appId;

    @ApiModelProperty("excel地址")
    private String excel;

    @ApiModelProperty("sheet编号，默认1")
    private Integer sheetNo;

}
