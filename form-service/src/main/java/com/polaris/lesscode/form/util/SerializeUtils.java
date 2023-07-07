package com.polaris.lesscode.form.util;

import java.lang.reflect.Field;

public class SerializeUtils {
	/**
	 * 获取对象字段属性值
	 * @param fieldName
	 * @param o
	 * @return
	 */
	public static Object getFieldValue(String fieldName, Object o){
		Class<?> clazz = o.getClass();
		Object obj = null;
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			obj = field.get(o);
		} catch (Exception e) {
			return null;
		}
		return obj;
	}
	/**
	 * 获取方法名
	 * @param head
	 * @param tail
	 * @return
	 */
	public static String getMethodName(String head, String tail){
		char[] ch = tail.toCharArray();
		if ((ch[0] >= 'a') && (ch[0] <= 'z')) {
			ch[0] = ((char)(ch[0] - ' '));
		}
		return head + new String(ch);
	}
}