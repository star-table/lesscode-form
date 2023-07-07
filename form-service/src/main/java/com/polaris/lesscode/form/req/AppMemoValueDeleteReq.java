package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="删除备忘录数据请求结构体", description="删除备忘录数据请求结构体")
public class AppMemoValueDeleteReq {

	@NotNull(message = "备忘录id不能为空")
	@ApiModelProperty("备忘录id")
	private List<Long> appMemoValueIds;

}
