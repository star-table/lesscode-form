package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 10:45 上午
 */
@Data
@ApiModel(value="保存表单返回结构体", description="保存表单返回结构体")
public class AppFormSaveResp {

    private Long id;

    private Long appId;

    private String config;
}
