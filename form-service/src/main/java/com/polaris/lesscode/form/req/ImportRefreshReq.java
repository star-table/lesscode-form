package com.polaris.lesscode.form.req;

import com.polaris.lesscode.form.dto.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ImportRefreshReq {

    @ApiModelProperty("导入的excel token")
    private String token;

    @ApiModelProperty("最新的columns信息")
    private List<Column> columns;

    @ApiModelProperty("sheet编号")
    private Integer sheetNo;

}
