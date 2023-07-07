package com.polaris.lesscode.form.openapi.model;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="表单数据集合请求结构体", description="表单数据集合请求结构体")
public class FilterValuesReq {

	@NotNull(message = "page不能为空")
	@ApiModelProperty("页码")
	private Integer page;

	@NotNull(message = "size不能为空")
	@ApiModelProperty("每页数量")
	private Integer size;

	@ApiModelProperty("字段列表")
	private List<String> fields;

	@ApiModelProperty("条件")
	private Condition filter;

	@ApiModelProperty("排序")
	private List<Order> sort;

}
