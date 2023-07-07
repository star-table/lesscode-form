package com.polaris.lesscode.form.internal.req;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.TableField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author Liu.B.J
 * @version 1.0
 * @date 2020-08-04 11:42 上午
 */
@Data
@ApiModel(value="动态表格设计请求信息(内部调用)", description="动态表格设计请求信息(内部调用)")
public class AppTableDesignSaveReq {

    private List<TableField> fields;

    private String name;

    private Integer groupType;

    private Long appId;


}
