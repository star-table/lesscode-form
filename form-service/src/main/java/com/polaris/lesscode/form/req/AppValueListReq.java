package com.polaris.lesscode.form.req;

import java.util.List;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="表单数据集合请求结构体", description="表单数据集合请求结构体")
public class AppValueListReq {

	@NotNull(message = "page不能为空")
	@ApiModelProperty("页码")
	private Integer page;

	@NotNull(message = "size不能为空")
	@ApiModelProperty("每页数量")
	private Integer size;

	@ApiModelProperty("字段列表")
	private List<String> columns;

	@ApiModelProperty("条件")
	private Condition condition;

	@ApiModelProperty("多个条件，and的关系")
	private Condition[] conditions;

	@ApiModelProperty("排序")
	private List<Order> orders;

	@ApiModelProperty("待办类型，2：审批，3：填写，4：抄送")
	private Integer todoType;

	@ApiModelProperty("待办状态，1：待办，2：通过/完成，3：拒绝")
	private Integer todoStatus;

	@ApiModelProperty("分组")
	private List<String> groups;

	@ApiModelProperty("重定向应用id，可以通过它们获取表头")
	private List<Long> redirectIds;

	@ApiModelProperty("需要查询的字段列表")
	private List<String> filterColumns;

	@ApiModelProperty("需要查询的表id")
	private Long tableId;

	private Long appId;

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
