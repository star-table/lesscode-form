package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="添加子表单数据请求结构体", description="添加子表单数据请求结构体")
public class SubValueAddReq {

	@ApiModelProperty("要插入的子数据")
	private List<Map<String, Object>> subform;

	private Long orgId;

	private Long userId;

}
