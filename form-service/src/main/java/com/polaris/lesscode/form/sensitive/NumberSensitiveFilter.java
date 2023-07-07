package com.polaris.lesscode.form.sensitive;

public class NumberSensitiveFilter implements SensitiveFilter{

	@Override
	public Object desensitization(Object o) {
		if(o == null) {
			return null;
		}
		return "*.**";
	}

}
