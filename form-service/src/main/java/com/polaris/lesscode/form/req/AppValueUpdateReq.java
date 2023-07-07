package com.polaris.lesscode.form.req;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="更新表单数据请求结构体", description="更新表单数据请求结构体")
public class AppValueUpdateReq {

	@ApiModelProperty("编辑的数据")
	private List<Map<String, Object>> form;

	@ApiModelProperty("重定向的应用id列表")
	private List<Long> redirectIds;

	@ApiModelProperty("需要查询的表id")
	private Long tableId;
}
