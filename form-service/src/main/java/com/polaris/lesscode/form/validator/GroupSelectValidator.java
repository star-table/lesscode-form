package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 单选下拉框校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@Slf4j
@ValidatorType(FieldTypeEnums.GROUP_SELECT)
@Component
public class GroupSelectValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if (fieldParam.getName().equals("issueStatus")) {
            numberValidate(fieldParam, value);
        }
//        Set<Object> values = getOptions(fieldParam);
//        log.info("[GroupSelectValidator] value: {}, options: {}", value, values);
//        if(! values.contains(value)){
//            throw ValidateErrors.errorf("%s值不匹配", fieldParam.getLabel());
//        }
    }
}
