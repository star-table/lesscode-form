/**
 * 
 */
package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

/**
 * @author Bomb.
 *    数据联动-显示
 */
@Data
public class DataRely {

	/**
	 * .依靠的控件
	 */
	private String relyFieldLable;
	
	private String relyFieldName;
	
	/**
	 * .关联的表单
	 */
	private String refFormName;
	
	private Long refAppId;
	
	/**
	 * .关联的匹配字段
	 */
	private String refEqualFieldLable;
	
	private String refEqualFieldName;
	
	/**
	 * .关联取值的字段
	 */
	private String refValueFieldLable;
	
	private String refValueFieldName;
	
	
	
	
}
