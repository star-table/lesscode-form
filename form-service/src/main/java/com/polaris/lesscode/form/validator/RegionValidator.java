package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

/**
 * 省市区校验
 * //todo 省市区数据结构
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.REGION)
@Component
public class RegionValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
    }
}
