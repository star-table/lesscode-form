package com.polaris.lesscode.form.annotations;

import com.polaris.lesscode.app.internal.enums.ActionType;
import com.polaris.lesscode.app.internal.enums.ActionObjType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ActionLogging {

	/**
	 * 应用id
	 *
	 **/
	String appIdExpress();

	/**
	 * 子表单字段key
	 **/
	String subformKeyExpress() default "";

	/**
	 * 子数据表达式
	 **/
	String subDataIdExpress() default "";

	/**
	 * 对象类型
	 **/
	ActionObjType objType();

	/**
	 * 动作
	 **/
	ActionType action();

	/**
	 * 数据id表达式
	 **/
	String dataIdExpress() default "";

	/**
	 * 数据表达式
	 **/
	String dataExpress() default "";
}
