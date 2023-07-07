package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="保存表单请求结构体", description="保存表单请求结构体")
public class AppFormSaveReq {

	private Long appId;
	
	private boolean drafted;
	
	private String config;
}
