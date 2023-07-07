package com.polaris.lesscode.form.internal.enums;

public enum YesOrNoEnum {

	YES(1, "是"),

	NO(2, "否");

	private int code;

	private String desc;

	private YesOrNoEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String name) {
		this.desc = desc;
	}

	public static YesOrNoEnum formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		YesOrNoEnum[] enums = values();
		for (YesOrNoEnum _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
