package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 多选框校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.MULTISELECT)
@Component
public class MultiSelectValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if(value == null) return;
        if (! (value instanceof Collection)){
            throw ValidateErrors.errorf("%s类型需要为list", fieldParam.getLabel());
        }
//        Set<Object> values = getOptions(fieldParam);
//        if(! values.containsAll((List<?>) value)){
//            throw ValidateErrors.errorf("%s值不匹配", fieldParam.getLabel());
//        }
    }
}
