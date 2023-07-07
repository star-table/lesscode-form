/**
 * 
 */
package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

/**
 * @author Bomb.
 * .数据关联过滤器
 */
@Data
public class DataLinkFilter {
	
	/**
	 * filter method.
	 */
	private String method;
	
	/**
	 * .控件过滤or固定值过滤   fixed or field.
	 */
	private String type;
	
	/**
	 * .关联字段
	 */
	private String linkField;
	
	/**
	 * .比较字段
	 */
	private String relyFiled;
	
	/**
	 *.比较字段类型. integer,string etc... 
	 */
	private String linkType;
	
	/**
	 * .比较固定值
	 */
	private Fixed fixed;
	
	
	/**
	 * .固定值过滤条件
	 * @author Bomb.
	 *
	 */
	@Data
	public static class Fixed {
		/**
		 * .数据类型  integer,string etc...
		 */
		private String type;
		
		/**
		 * .条件.
		 */
		private String value;
	}
}
