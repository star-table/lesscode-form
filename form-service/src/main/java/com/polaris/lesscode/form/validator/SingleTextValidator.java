package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

/**
 * 单行文本校验
 *
 * @author Nico
 * @date 2021/1/27 17:12
 */
@ValidatorType(FieldTypeEnums.SINGLE_TEXT)
@Component
public class SingleTextValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
//        dataTypeValidate(fieldParam, value, String.class);
        lenValidate(fieldParam, value);
        regexValidate(fieldParam, value);
    }
}
