package com.polaris.lesscode.form.internal.enums;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public enum NumberFormatEnum {

	//1：无 2：显示千位分隔符 3：显示百分比

	NONE(1, "无"),

	THOUSANDTH(2, "显示千位分隔符"),

	PERCENTAGE(3, "显示百分比");

	private Integer type;

	private String desc;

	NumberFormatEnum(Integer type, String desc) {
		this.type = type;
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

	public static NumberFormatEnum formatOrNull(Integer type) {
		if (type == null) {
			return null;
		}
		NumberFormatEnum[] enums = values();
		for (NumberFormatEnum _enu : enums) {
			if (_enu.getType().equals(type)) {
				return _enu;
			}
		}
		return null;
	}

	public static Boolean checkNumberFormat(String num, Integer type){
		boolean flag = true;
		if(! StringUtils.isBlank(num)){
			if(type != null){
				try{
					if(type.equals(NumberFormatEnum.THOUSANDTH.getType())){
						Double b = NumberFormat.getNumberInstance(Locale.getDefault()).parse(num).doubleValue();    //转为数字
						String str = NumberFormat.getNumberInstance(Locale.getDefault()).format(b);     //转为千分符字符串
						if(! num.equals(str)){
							return false;
						}
					}else if(type.equals(NumberFormatEnum.PERCENTAGE.getType())){
						NumberFormat nf = NumberFormat.getPercentInstance();
						nf.parse(num);
					}
				}catch (ParseException e){
					flag = false;
				}
			}
		}
		return flag;
	}
	
}
