package com.polaris.lesscode.form.resp;

import com.alibaba.excel.read.metadata.ReadSheet;
import com.polaris.lesscode.form.dto.Column;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ImportPreResp {

    @ApiModelProperty("导入需要的令牌，带有效期")
    private String token;

    @ApiModelProperty("有效期")
    private Date expired;

    @ApiModelProperty("列信息")
    private List<Column> columns;

    @ApiModelProperty("sheet信息")
    private List<ReadSheet> sheets;

    @ApiModelProperty("样例数据")
    private List<Map<String, Object>> samples;
}
