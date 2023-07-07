package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="编辑子表单数据请求结构体", description="编辑子表单数据请求结构体")
public class SubValueUpdateReq {

	@ApiModelProperty("要更新的子数据")
	private List<Map<String, Object>> subform;

	private Long orgId;

	private Long userId;

}
