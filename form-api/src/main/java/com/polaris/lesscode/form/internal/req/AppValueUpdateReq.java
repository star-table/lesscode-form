package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="更新表单数据请求结构体", description="更新表单数据请求结构体")
public class AppValueUpdateReq {

	@ApiModelProperty("要更新的数据")
	private List<Map<String, Object>> form;

	@ApiModelProperty("重定向的应用id列表")
	private List<Long> redirectIds;

	private Long orgId;

	private Long userId;

	private Long tableId;
}
