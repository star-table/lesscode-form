package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="表单附件返回结构体", description="表单附件返回结构体")
public class EnclosureResp {

	private String name;

	private String url;

}
