package com.polaris.lesscode.form.req;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Order;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value="表单数据导出excel请求结构体", description="表单数据导出excel请求结构体")
public class AppValueExportReq{

	private List<String> columns;

	private Condition condition;

	private List<Order> orders;

	private List<String> groups;
	
}
