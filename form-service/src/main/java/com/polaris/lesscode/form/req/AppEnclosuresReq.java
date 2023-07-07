package com.polaris.lesscode.form.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="表单附件请求结构体", description="表单附件请求结构体")
public class AppEnclosuresReq {

	private Condition condition;

	@NotBlank(message = "附件字段不能为空")
	private String column;

}
