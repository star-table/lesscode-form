package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@ValidatorType(FieldTypeEnums.WORK_HOUR)
@Component
@Slf4j
public class WorkHourValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        if(value == null) return;

        if (! (value instanceof Map)){
            throw ValidateErrors.errorf("%s数据格式错误，应为自定义结构", fieldParam.getLabel());
        }

        Map m = (Map)value;
        if (!m.containsKey("planHour")) {
            throw ValidateErrors.errorf("%s数据格式错误，planHour不存在", fieldParam.getLabel());
        }
        if (!m.containsKey("actualHour")) {
            throw ValidateErrors.errorf("%s数据格式错误，actualHour不存在", fieldParam.getLabel());
        }
        if (!m.containsKey(FormFieldConstant.COLLABORATOR_IDS)) {
            throw ValidateErrors.errorf("%s数据格式错误，collaboratorIds不存在", fieldParam.getLabel());
        }
        if (!(m.get(FormFieldConstant.COLLABORATOR_IDS) instanceof Collection)) {
            throw ValidateErrors.errorf("%s数据格式错误，collaboratorIds必须是数组", fieldParam.getLabel());
        }
    }
}
