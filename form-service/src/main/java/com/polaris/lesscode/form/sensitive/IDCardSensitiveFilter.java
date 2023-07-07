package com.polaris.lesscode.form.sensitive;

public class IDCardSensitiveFilter implements SensitiveFilter{

	@Override
	public Object desensitization(Object o) {
		if(o == null) {
			return null;
		}
		return String.valueOf(o).replaceAll("(?<=\\w{6})\\w(?=\\w{6})", "*");
	}

}
