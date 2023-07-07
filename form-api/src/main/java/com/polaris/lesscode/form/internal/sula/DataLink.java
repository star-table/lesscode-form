package com.polaris.lesscode.form.internal.sula;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Bomb.
 * .关联数据
 */
@Data
public class DataLink {

	/**
	 * link form name.
	 */
	private String linkForm;
	
	/**
	 * link key/column name
	 */
	private String linkKey;
	
	/**
	 * link type.
	 */
	private String linkType;
	
	/**
	 * allow add new linked form data.
	 */
	private Boolean isAllowAddFlg;
	
	/**
	 * show linked fields.
	 */
	private List<LinkField> linkFields;
	
	/**
	 * data filters.
	 */
	private List<DataLinkFilter> filters;
	
	@Data
	public static class LinkField{
		
		/**
		 * field name.
		 */
		private String name;
		
		/**
		 * field show text.
		 */
		private String text;
		
		/**
		 * field type.
		 */
		private String type;
	}
}
