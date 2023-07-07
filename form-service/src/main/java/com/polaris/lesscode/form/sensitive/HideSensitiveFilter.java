package com.polaris.lesscode.form.sensitive;

public class HideSensitiveFilter implements SensitiveFilter{

	@Override
	public Object desensitization(Object o) {
		return "";
	}

}
