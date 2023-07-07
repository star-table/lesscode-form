package com.polaris.lesscode.form.sensitive;

import org.apache.commons.lang3.StringUtils;

public class TextSensitiveFilter implements SensitiveFilter{

	@Override
	public Object desensitization(Object o) {
		if(o == null) {
			return null;
		}
		String fullName = String.valueOf(o);
	    String name = StringUtils.left(fullName, 1);
	    return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
	}

}
