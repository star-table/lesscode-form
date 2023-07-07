package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="添加表单数据请求结构体", description="添加表单数据请求结构体")
public class AppValueAddReq {

	@ApiModelProperty("要插入的数据")
	private List<Map<String, Object>> form;

	@ApiModelProperty("前面的数据id")
	private Long beforeId;

	@ApiModelProperty("后面的数据id")
	private Long afterId;

	@ApiModelProperty("重定向的应用id列表")
	private List<Long> redirectIds;

	@ApiModelProperty("如果为true，则认为当前排序规则为升序，默认为true")
	private boolean asc = true;

	private Long orgId;

	private Long userId;

	private Long tableId;

	@ApiModelProperty("是否是导入")
	private boolean isImport;

	@ApiModelProperty("是否是创建模板")
	private boolean isCreateTemplate;
}
