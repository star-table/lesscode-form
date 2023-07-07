package com.polaris.lesscode.form.sensitive;

public class NothingToDoSensitiveFilter implements SensitiveFilter{

	@Override
	public Object desensitization(Object o) {
		return o;
	}

}
