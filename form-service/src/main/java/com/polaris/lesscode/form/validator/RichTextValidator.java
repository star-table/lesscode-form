package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 富文本校验
 *
 * @Author Nico
 * @Date 2021/1/28 13:44
 **/
@ValidatorType(FieldTypeEnums.RICH_TEXT)
@Component
public class RichTextValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        lenValidate(fieldParam, value);
    }
}
