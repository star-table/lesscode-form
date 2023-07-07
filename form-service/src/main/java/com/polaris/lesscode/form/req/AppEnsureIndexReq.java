package com.polaris.lesscode.form.req;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="添加默认索引请求结构体", description="添加默认索引请求结构体")
public class AppEnsureIndexReq {

	/**
	 * 是否添加默认索引
	 */
	private boolean ensureDefaultIndexes;
	
	/**
	 * 字段列表，内部list中的字段为组合索引
	 */
	private List<String[]> columnsList;
}
