package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="删除表单数据请求结构体", description="删除表单数据请求结构体")
public class AppValueDeleteReq {

	private List<Long> appValueIds;

}
