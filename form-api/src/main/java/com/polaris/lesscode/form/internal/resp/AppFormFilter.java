package com.polaris.lesscode.form.internal.resp;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="表单过滤响应(内部调用)", description="表单过滤响应(内部调用)")
public class AppFormFilter {

	private List<Long> appIds;
	
	private Long orgId;
	
	private Integer delFlag;

}
