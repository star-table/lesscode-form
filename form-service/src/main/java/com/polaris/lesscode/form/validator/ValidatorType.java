package com.polaris.lesscode.form.validator;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author: Liu.B.J
 * @date: 2021/1/27 11:48
 * @description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatorType {
    FieldTypeEnums value();
}
