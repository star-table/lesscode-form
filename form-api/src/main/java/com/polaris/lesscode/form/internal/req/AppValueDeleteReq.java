package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="删除表单数据请求结构体", description="删除表单数据请求结构体")
public class AppValueDeleteReq {

	@ApiModelProperty("要删除的数据id")
	private List<Long> appValueIds;

	@ApiModelProperty("任务id")
	private List<Long> issueIds;

	private Long appId;

	private Long orgId;

	private Long userId;

	private Long tableId;

}
