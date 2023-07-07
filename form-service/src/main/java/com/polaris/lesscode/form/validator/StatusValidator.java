package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

/**
 * 开关校验
 *
 * @Author Nico
 * @Date 2021/1/28 10:30
 **/
@ValidatorType(FieldTypeEnums.STATUS)
@Component
public class StatusValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        numberValidate(fieldParam, value);
        if(! CommonConsts.TRUE.equals(value) && ! CommonConsts.FALSE.equals(value)){
            throw ValidateErrors.errorf("开关值无效，有效值为1和2");
        }
    }
}
