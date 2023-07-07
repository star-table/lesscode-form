package com.polaris.lesscode.form.internal.enums;

import com.polaris.lesscode.util.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;

public enum DateTypeEnum {

	YYYYMMDD(1, DateFormatUtil.EL_YYYY_MM_DD, "YYYY-MM-DD"),

	YYYYMMDDHHmm(2, DateFormatUtil.EL_YYYY_MM_DD_HH_MM, "YYYY-MM-DD HH:mm"),

	YYYYMMDDHHmmss(3, DateFormatUtil.EL_YYYY_MM_DD_HH_MM_SS, "YYY-MM-DD HH:mm:ss");

	private int type;

	private String el;

	private String desc;

	DateTypeEnum(int type, String el, String desc) {
		this.type = type;
		this.el = el;
		this.desc = desc;
	}

	public Integer getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEl() {
		return el;
	}

	public void setEl(String el) {
		this.el = el;
	}

	public static DateTypeEnum formatOrNull(Integer type) {
		if (type == null) {
			return null;
		}
		DateTypeEnum[] enums = values();
		for (DateTypeEnum _enu : enums) {
			if (_enu.getType().equals(type)) {
				return _enu;
			}
		}
		return null;
	}
	
}
