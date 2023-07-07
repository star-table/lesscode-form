package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 数字文本校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.RELATION_TABLE)
@Component
public class RelationTableValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if (value != null){
            if (! (value instanceof Collection)){
                throw ValidateErrors.errorf("%s数据格式错误，应为数组", fieldParam.getLabel());
            }
            for (Object o: (Collection) value){
                if (! StringUtils.isNumeric(String.valueOf(o))){
                    throw ValidateErrors.errorf("%s数据类型错误，应为ID数组", fieldParam.getLabel());
                }
            }
        }

    }
}
