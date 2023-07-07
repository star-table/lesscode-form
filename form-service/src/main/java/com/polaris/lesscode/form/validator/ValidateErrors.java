package com.polaris.lesscode.form.validator;

/**
 * 校验错误
 *
 * @author Nico
 * @date 2021/1/27 17:22
 */
public interface ValidateErrors {

    ValidateError StringBlankError = new ValidateError("文本为空");

    static ValidateError errorf(String msg, Object... args){
        return new ValidateError(String.format(msg, args));
    }
}
