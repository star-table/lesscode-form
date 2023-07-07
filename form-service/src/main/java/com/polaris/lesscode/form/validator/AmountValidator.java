package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

/**
 * 金额校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.AMOUNT)
@Component
public class AmountValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        // 金额为字符串
//        dataTypeValidate(fieldParam, value, String.class);
        // 字符串内容需为number
        numberValidate(fieldParam, value);
        rangeNumberValidate(fieldParam, value);
    }
}
