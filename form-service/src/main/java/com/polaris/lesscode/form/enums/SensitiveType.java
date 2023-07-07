package com.polaris.lesscode.form.enums;


import com.polaris.lesscode.form.sensitive.*;

public enum SensitiveType {
	MOBILE("手机号脱敏策略", new MobileSensitiveFilter()),
	EMAIL("邮箱脱敏策略", new EmailSensitiveFilter()),
	NUMBER("数字脱敏策略", new NumberSensitiveFilter()),
	TEXT("姓名脱敏策略", new TextSensitiveFilter()),
	DEFAULT("默认脱敏策略", new TextSensitiveFilter()),
	;

	/**
	 * name
	 */
	private String name;

	/**
	 * repx
	 */
	private SensitiveFilter filter;
	
	private SensitiveType(String name, SensitiveFilter filter) {
		this.name = name;
		this.filter = filter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SensitiveFilter getFilter() {
		return filter;
	}

	public void setFilter(SensitiveFilter filter) {
		this.filter = filter;
	}

	public static SensitiveType parse(String value) {
		try {
			return SensitiveType.valueOf(value.toUpperCase());
		}catch (Exception e) {
		}
		return SensitiveType.DEFAULT;
	}
}
