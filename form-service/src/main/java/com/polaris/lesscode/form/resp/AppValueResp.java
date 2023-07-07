package com.polaris.lesscode.form.resp;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="表单数据返回结构体", description="表单数据返回结构体")
public class AppValueResp {

	private Long id;
	
	private Long appId;
	
	private String content;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;
	
}
