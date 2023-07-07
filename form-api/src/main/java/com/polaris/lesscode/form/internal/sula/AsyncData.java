/**
 * 
 */
package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

/**
 * @author Bomb.
 * 
 */
@Data
public class AsyncData {
	
	/**
	 * .请求路径,统一路径，前期可以忽律
	 */
	private String url;

	/**
	 * .表单id
	 */
	private String appName;
	
	/**
	 * .表单名称
	 */
	private Long appId;
	
	/**
	 * 
	 */
	private String fieldLable;
	
	/**
	 * .字段名称
	 */
	private String fieldName;
	
	/**
	 * .值排序
	 */
	private boolean isAsc;
	
	/**
	 * 
	 */
	private String orderFieldName;
	
}
