package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.bo.MemberFieldDatas;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.util.MemberFieldTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 成员校验
 * // TODO 成员校验待确认
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.USER)
@Component
@Slf4j
public class MemberValidator extends AbstractValidator {

    private static final String PROPS_MULTIPLE_PARAM_NAME = FormConstant.PROPS_MULTIPLE_PARAM_NAME;
    private static final boolean DEFAULT_PROPS_MULTIPLE_PARAM_VALUE = false;

    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError {
        requiredValidate(fieldParam, value);
        if (Objects.isNull(value)) {
            return;
        }
        if (!(value instanceof Collection)) {
            log.error("[成员类型控件校验] -> 数据类型错误");
            throw ValidateErrors.errorf("%s字段只可选择人员/部门/角色", fieldParam.getLabel());
        }
        Map<String, Object> props = fieldParam.getField().getProps();
        List<?> values = new ArrayList<>((Collection<?>) value);

        // 不允许多选
//        if (Objects.nonNull(props)) {
//            Object memberPropsObj = props.get(fieldParam.getField().getType());
//            if (memberPropsObj instanceof Map){
//                Object multipleObj = ((Map<?, ?>) memberPropsObj).get("multiple");
//                if (Objects.equals(multipleObj, false)){
//                    if (values.size() > 1) {
//                        throw ValidateErrors.errorf("%s字段不允许多选", fieldParam.getLabel());
//                    }
//                }
//            }
//        }
        MemberFieldDatas dataList = MemberFieldTypeUtil.parseMemberFieldDataList(values.stream().map(String::valueOf).collect(Collectors.toList()));
        if (dataList.hasInvalidListItem()) {
            log.error("[成员控件数据验证] -> 无效id {}", dataList.getInvalidList());
            throw ValidateErrors.errorf("%s字段只可选择人员/部门/角色", fieldParam.getLabel());
        }

    }
}
