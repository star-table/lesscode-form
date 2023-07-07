package com.polaris.lesscode.form.sensitive;

public class MobileSensitiveFilter implements SensitiveFilter {

	@Override
	public Object desensitization(Object o) {
		if(o == null) {
			return null;
		}
		return String.valueOf(o).replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

}
