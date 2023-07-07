package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="添加子表单数据请求结构体", description="添加子表单数据请求结构体")
public class SubValueAddReq {

	private List<Map<String, Object>> subform;

}
