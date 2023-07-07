package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.sula.Field;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.RuleParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: Liu.B.J
 * @date: 2021/1/27 11:49
 * @description:
 */
public abstract class AbstractValidator {

    public abstract void validate(FieldParam fieldParam, Object value) throws ValidateError;

    protected void requiredValidate(FieldParam fieldParam, Object value) throws ValidateError{
        return;
//        if (! fieldParam.getWritable() && value != null){
//            throw ValidateErrors.errorf("%s字段不允许写入", fieldParam.getLabel());
//        }
//
//        Map<String, Object> props = fieldParam.getField().getProps();
//        // 暂时兼容前端...
//        if(MapUtils.isNotEmpty(props)){
//            if(props.containsKey("required") && Objects.equals(true, props.get("required"))){
//                assertRequired(fieldParam, value);
//            }else if(props.containsKey("require") && Objects.equals(true, props.get("require"))){
//                assertRequired(fieldParam, value);
//            }
//        }
//
//        if(hasNotRules(fieldParam)){
//            return;
//        }
//        for(RuleParam rule: fieldParam.getRules()){
//            if(rule.getRequired() != null && rule.getRequired()){
//                assertRequired(fieldParam, value);
//                break;
//            }
//        }
//        return;
    }

    protected void assertRequired(FieldParam fieldParam, Object value) throws ValidateError{
        if(value == null){
            throw ValidateErrors.errorf("%s字段必填", fieldParam.getLabel());
        }
        if(value instanceof String && StringUtils.isBlank((String)value)){
            throw ValidateErrors.errorf("%s字段必填", fieldParam.getLabel());
        }
        if(value instanceof Collection){
            if(CollectionUtils.isEmpty((Collection<?>) value)){
                throw ValidateErrors.errorf("%s字段必选", fieldParam.getLabel());
            }
        }
    }

    protected void numberValidate(FieldParam fieldParam, Object value) throws ValidateError{
        if(value != null){
            try{
                Double.valueOf(String.valueOf(value));
            }catch(NumberFormatException e){
                throw ValidateErrors.errorf("%s字段值必须为数字", fieldParam.getLabel());
            }
        }
        return;
    }

    protected void rangeNumberValidate(FieldParam fieldParam, Object value) throws ValidateError{
        numberValidate(fieldParam, value);
        if(hasNotRules(fieldParam)){
            return;
        }
        BigDecimal decimal = new BigDecimal(String.valueOf(value));
        for(RuleParam rule: fieldParam.getRules()){
            if(rule.getMin() != null && decimal.compareTo(rule.getMin()) == -1){
                throw ValidateErrors.errorf("%s字段值不能小于%d", fieldParam.getLabel(), rule.getMin());
            }
            if(rule.getMax() != null && decimal.compareTo(rule.getMin()) == 1){
                throw ValidateErrors.errorf("%s字段值不能大于%d", fieldParam.getLabel(), rule.getMax());
            }
        }
        return;
    }

    protected void lenValidate(FieldParam fieldParam, Object value) throws ValidateError{
        if(hasNotRules(fieldParam)){
            return;
        }
        for(RuleParam rule: fieldParam.getRules()){
            if(rule.getMinLen() != null && rule.getMinLen() > 0 && (value == null || String.valueOf(value).length() < rule.getMinLen())){
                throw ValidateErrors.errorf("%s字段值长度不能小于%d", fieldParam.getLabel(), rule.getMinLen());
            }
            if(rule.getMaxLen() != null && rule.getMaxLen() > 0 && (value == null || String.valueOf(value).length() > rule.getMaxLen())){
                throw ValidateErrors.errorf("%s字段值长度不能大于%d", fieldParam.getLabel(), rule.getMinLen());
            }
        }
        return;
    }

    protected void dataTypeValidate(FieldParam fieldParam, Object value, Class<?> ...clazz) throws ValidateError{
        if(value == null || clazz.length == 0){
            return;
        }
        for(Class<?> c: clazz){
            if(value.getClass().isAssignableFrom(c)){
                return;
            }
        }
        throw ValidateErrors.errorf("%s字段值类型应该为%s", fieldParam.getLabel(), clazz[0].getName());
    }

    protected void regexValidate(FieldParam fieldParam, Object value) throws ValidateError{
        if(value == null || hasNotRules(fieldParam)){
            return;
        }
        String text = String.valueOf(value);
        for(RuleParam rule: fieldParam.getRules()){
            if(StringUtils.isNotBlank(rule.getRegex())){
                if(! Pattern.matches(rule.getRegex(), text)){
                    throw ValidateErrors.errorf("%s字段值不匹配正则表达式%s", fieldParam.getLabel(), rule.getMax());
                }
            }
        }
        return;
    }

    protected void dateValidate(FieldParam fieldParam, Object value) throws ValidateError{
        if (value == null) {
            return;
        }
        String text = String.valueOf(value);
        if (text.isEmpty()) {
            return;
        }
        String pattern = "yyyy-MM-dd hh:mm:ss"; // "2023-05-23 14:47:42"
        try {
            new SimpleDateFormat(pattern).parse(text);
        } catch (ParseException e) {
            throw ValidateErrors.errorf("%s日期格式非法", fieldParam.getLabel());
        }
        return;
    }

    protected Set<Object> getOptions(FieldParam fieldParam) throws ValidateError {
        Map<String, Object> props = fieldParam.getField().getProps();
        if(MapUtils.isEmpty(props)){
            throw ValidateErrors.errorf("%s值未定义", fieldParam.getLabel());
        }

        Object selectObj = props.get(fieldParam.getField().getType());
        if(! (selectObj instanceof Map)){
            throw ValidateErrors.errorf("%s值未定义", fieldParam.getLabel());
        }

        Map<?, ?> select = (Map<?, ?>) selectObj;
        Object optionsObj = select.get("options");
        if(! (optionsObj instanceof List)){
            throw ValidateErrors.errorf("%s值未定义", fieldParam.getLabel());
        }

        List<?> options = (List<?>) optionsObj;
        Set<Object> values = new HashSet<>();
        for(Object optionObj: options){
            if((!(optionObj instanceof Map))){
                throw ValidateErrors.errorf("%s值未定义", fieldParam.getLabel());
            }
            Map<?, ?> option = (Map<?, ?>) optionObj;
            values.add(option.get("id"));
        }
        return values;
    }



    private boolean hasNotRules(FieldParam fieldParam){
        return CollectionUtils.isEmpty(fieldParam.getRules());
    }

}
