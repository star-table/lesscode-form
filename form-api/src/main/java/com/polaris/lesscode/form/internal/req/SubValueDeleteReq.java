package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel(value="删除子表单数据请求结构体", description="删除子表单数据请求结构体")
public class SubValueDeleteReq {

	@NotEmpty(message = "请选择需要删除的数据")
	private List<Long> subformValueIds;

	private Long orgId;

	private Long userId;

}
