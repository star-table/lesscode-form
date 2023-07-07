package com.polaris.lesscode.form.internal.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="表单数据集合请求结构体", description="表单数据集合请求结构体")
public class AppValueListReq {

	@NotNull(message = "page不能为空")
	private Integer page;

	@NotNull(message = "size不能为空")
	private Integer size;

	private List<String> columns;

	private Condition condition;

	private Condition[] conditions;

	private List<Order> orders;

	private List<String> groups;

	private Long orgId;

	private Long userId;

	private Long tableId;

	private Long appId;

	@ApiModelProperty("重定向应用id，可以通过它们获取表头")
	private List<Long> redirectIds;

	@ApiModelProperty("需要查询的字段列表")
	private List<String> filterColumns;

	// 是否是导出，导出的话，不限制size
	private boolean export;

	// 需要总数
	private boolean needTotal;

	// 是否需要引用列数据
	private boolean needRefColumn;

	// 收集原值是否限制，默认限制10条
	private boolean aggNoLimit;

	// 是否需要删除的数据
	private boolean needDeleteData;
}
