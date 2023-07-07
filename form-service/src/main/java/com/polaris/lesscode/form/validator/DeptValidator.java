package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门校验
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.DEPT)
@Component
@Slf4j
public class DeptValidator extends AbstractValidator {


    private static final String PROPS_MULTIPLE_PARAM_NAME = FormConstant.PROPS_MULTIPLE_PARAM_NAME;
    private static final boolean DEFAULT_PROPS_MULTIPLE_PARAM_VALUE = false;

    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError {
        requiredValidate(fieldParam, value);

        if (Objects.isNull(value)) {
            return;
        }
        if (!(value instanceof Collection)) {
            log.error("[部门类型控件校验] -> 数据类型错误");
            throw ValidateErrors.errorf("%s字段只可选择部门", fieldParam.getLabel());
        }
        Map<String, Object> props = fieldParam.getField().getProps();
        List<?> values = new ArrayList<>((Collection<?>) value);
        // 不允许多选
//        if (Objects.isNull(props) || !(Boolean) props.getOrDefault(PROPS_MULTIPLE_PARAM_NAME, DEFAULT_PROPS_MULTIPLE_PARAM_VALUE)) {
//            if (values.size() > 1) {
//                throw ValidateErrors.errorf("%s字段不允许多选", fieldParam.getLabel());
//            }
//        }
        List<String> ids = values.stream().map(String::valueOf).collect(Collectors.toList());
        for (String id : ids) {
            numberValidate(fieldParam, id);
        }

    }
}
