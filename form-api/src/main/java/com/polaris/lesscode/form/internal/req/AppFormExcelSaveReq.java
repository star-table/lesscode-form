package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 11:42 上午
 */
@Data
@ApiModel(value="表单Excel导入请求信息(内部调用)", description="表单Excel导入请求信息(内部调用)")
public class AppFormExcelSaveReq {

    private List<Map<String, Object>> config;

    private String name;

    private Integer type;

    private Integer groupType;

    private Long pkgId;
}
