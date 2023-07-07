package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-05 9:21 上午
 */
@Data
@ApiModel(value="导入表单返回结构体", description="导入表单返回结构体")
public class AppImportResp {

    private Integer totalCount;

    private Integer successCount;
}
