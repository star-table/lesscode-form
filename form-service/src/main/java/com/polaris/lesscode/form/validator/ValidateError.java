package com.polaris.lesscode.form.validator;

import lombok.Data;

/**
 * 校验错误信息
 *
 * @author Nico
 * @date 2021/1/27 17:19
 */
@Data
public class ValidateError extends Exception{

    public ValidateError(String message) {
        super(message);
    }

    public ValidateError() { }

}
