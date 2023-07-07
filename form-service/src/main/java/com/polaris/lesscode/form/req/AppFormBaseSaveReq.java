package com.polaris.lesscode.form.req;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AppFormBaseSaveReq {

    @ApiModelProperty("要修改或新增的表头配置")
    private List<FieldParam> added;

    @ApiModelProperty("要删除的表头")
    private Set<String> deleted;
}
