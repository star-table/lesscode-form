package com.polaris.lesscode.form.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="备忘录数据集合请求结构体", description="备忘录数据集合请求结构体")
public class AppMemoValueListReq {

	@NotNull(message = "page不能为空")
	private Integer page;

	@NotNull(message = "size不能为空")
	private Integer size;

	@ApiModelProperty("指定查询条件")
	private Boolean appoint = false;

	@ApiModelProperty("查询条件")
	private Condition condition;

	@ApiModelProperty("被查询列（空 查所有）")
	private List<String> columns;

	@ApiModelProperty("分组")
	private List<String> groups;
	
	//private List<AppValueCondition> conditions;
	
	//private List<AppValueOrder> orders;
}
