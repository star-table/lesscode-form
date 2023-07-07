package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 图片校验
 *
 * @Author Nico
 * @Date 2021/1/28 13:45
 **/
@ValidatorType(FieldTypeEnums.IMAGE)
@Component
public class ImageValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if(value == null) return;
        imageValidate(fieldParam, value);
    }

    protected void imageValidate(FieldParam fieldParam, Object value) throws ValidateError {
        Collection<Object> valueList = new ArrayList<>();
        if (value instanceof Collection) {
            valueList = (Collection)value;
        } else {
            throw ValidateErrors.errorf("%s数据结构异常", fieldParam.getLabel());
        }
        for (Object linkObj: valueList) {
            if (linkObj instanceof Map) {
                Map<?, ?> link = (Map<?, ?>) linkObj;
                Object name = link.get("name");
                Object path = link.get("path");
                if (name == null || org.apache.commons.lang3.StringUtils.isEmpty(String.valueOf(name))) {
                    throw ValidateErrors.errorf("%s名称不能为空", fieldParam.getLabel());
                }
                if (path == null || org.apache.commons.lang3.StringUtils.isBlank(String.valueOf(path))) {
                    throw ValidateErrors.errorf("%s链接不能为空", fieldParam.getLabel());
                }
                try {
                    new URI(String.valueOf(path));
                } catch (URISyntaxException e) {
                    throw ValidateErrors.errorf("%s链接格式错误", fieldParam.getLabel());
                }
            } else {
                throw ValidateErrors.errorf("%s链接格式错误", fieldParam.getLabel());
            }
        }
    }
}
