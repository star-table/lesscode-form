package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.polaris.lesscode.form.bo.Relating.LINK_TO;
import static com.polaris.lesscode.form.bo.Relating.LINK_FROM;

@ValidatorType(FieldTypeEnums.RELATING)
@Component
public class RelatingValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError {
        requiredValidate(fieldParam, value);
        if (value != null) {
            if (!(value instanceof Map)) {
                throw ValidateErrors.errorf("%s数据格式错误，应为自定义结构", fieldParam.getLabel());
            }
            Map m = (Map)value;

            for (Object s :m.entrySet()) {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)s;
                Object keyO = entry.getKey();
                Object valueO = entry.getValue();


                if (!(keyO instanceof String)) {
                    throw ValidateErrors.errorf("%s数据类型错误，%s字段类型非法", fieldParam.getLabel(), keyO);
                }
                String key = (String)keyO;
                if (key.equals(LINK_TO)) {
                    if (!(valueO instanceof Collection)) {
                        throw ValidateErrors.errorf("%s数据类型错误，%s不是数组:%s", fieldParam.getLabel(), key, valueO);
                    }
                    // 去重
                    HashSet<String> ids = new HashSet<>();
                    for (Object o: (Collection) valueO) {
                        if (!(o instanceof String)) {
                            throw ValidateErrors.errorf("%s数据类型错误，%s应为ID字符串数组:%s", fieldParam.getLabel(), key, o);
                        } else {
                            ids.add((String) o);
                        }
                    }
                    m.put(key, new ArrayList<>(ids));
                } else if (key.equals(LINK_FROM)) {
                    if (!(valueO instanceof Collection)) {
                        throw ValidateErrors.errorf("%s数据类型错误，%s不是数组:%s", fieldParam.getLabel(), key, valueO);
                    }
                    // 去重
                    HashSet<String> ids = new HashSet<>();
                    for (Object o: (Collection) valueO) {
                        if (!(o instanceof String)) {
                            throw ValidateErrors.errorf("%s数据类型错误，%s应为ID字符串数组:%s", fieldParam.getLabel(), key, o);
                        } else {
                            ids.add((String) o);
                        }
                    }
                    m.put(key, new ArrayList<>(ids));
                } else {
                    throw ValidateErrors.errorf("%s数据结构错误，%s是非法字段:%s", fieldParam.getLabel(), key, valueO);
                }
            }
        }
    }
}
