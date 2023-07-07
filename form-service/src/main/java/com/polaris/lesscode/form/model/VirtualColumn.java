package com.polaris.lesscode.form.model;

import lombok.Data;

@Data
public class VirtualColumn {

	/**
	 * 虚拟列名字
	 */
	private String name;
	
	/**
	 * 虚拟列类型
	 */
	private String type;
	
	/**
	 * 映射的jsonpath
	 */
	private String jsonPath;
	
	/**
	 * 索引名称
	 */
	private String indexName;
	
	/**
	 * 索引
	 */
	private boolean isNormalIndex;
	
	/**
	 * 索引
	 */
	private boolean isArrayIndex;

}
