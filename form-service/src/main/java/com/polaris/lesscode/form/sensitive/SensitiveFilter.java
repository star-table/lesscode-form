package com.polaris.lesscode.form.sensitive;

/**
 * 敏感过滤器
 *
 * @author nico
 * @date 2020-02-03 12:15
 */
public interface SensitiveFilter {
	
	public Object desensitization(Object o);
}
