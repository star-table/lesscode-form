package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 附件校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.DOCUMENT)
@Component
public class DocumentValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if(value == null) return;
        documentValidate(fieldParam, value);
    }

    protected void documentValidate(FieldParam fieldParam, Object value) throws ValidateError{
        if (value == null) {
            return;
        }
        Collection<Object> keyList;
        Collection<Object> valueList;
        if (value instanceof Map) {
            keyList = Arrays.asList(((Map<?, Object>) value).keySet().toArray());
            valueList = ((Map<?, Object>) value).values();
        } else {
            throw ValidateErrors.errorf("%s数据结构异常", fieldParam.getLabel());
        }

        for (Object key: keyList) {
            if (!(key instanceof String)) {
                throw ValidateErrors.errorf("%s数据结构异常", fieldParam.getLabel());
            }
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
//                try {
//                    new URI(String.valueOf(path));
//                } catch (URISyntaxException e) {
//                    throw ValidateErrors.errorf("%s链接格式错误", fieldParam.getLabel());
//                }
            } else {
                throw ValidateErrors.errorf("%s链接格式错误", fieldParam.getLabel());
            }
        }
    }
}
