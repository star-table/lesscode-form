package com.polaris.lesscode.form.resp;

import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.internal.sula.Column;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表单配置响应结构体
 *
 * @author Nico
 * @date 2021/3/11 19:40
 */
@Data
public class AppFormConfigResp {
    @ApiModelProperty("appId")
    private Long appId;

    @ApiModelProperty("fields")
    private List<FieldParam> fields;

    @ApiModelProperty("columns")
    private List<Column> columns;

    @ApiModelProperty("字段排序")
    private Map<String, Integer> fieldOrders;

    @ApiModelProperty("相关的应用信息，key为appId, value为表单配置")
    private Map<Long, AppResp> relevantApps;

    @ApiModelProperty("相关的表单信息，key为appId, value为表单配置")
    private Map<Long, BizForm> relevantForms;

    @ApiModelProperty("自定义配置")
    private Map<String, Object> customConfig;
}
