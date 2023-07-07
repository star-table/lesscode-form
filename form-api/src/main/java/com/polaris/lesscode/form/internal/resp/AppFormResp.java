package com.polaris.lesscode.form.internal.resp;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="表单响应(内部调用)", description="表单响应(内部调用)")
public class AppFormResp {

	private Long id;
	
	private Long orgId;
	
	private Long appId;

	private Integer type;

	private String config;

	private Integer status;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;
	
}
