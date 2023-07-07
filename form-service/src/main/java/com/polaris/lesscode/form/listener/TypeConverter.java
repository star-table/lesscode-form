package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.validator.ValidateError;
import com.polaris.lesscode.form.validator.ValidateErrors;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public abstract class TypeConverter {

    public abstract Object parse(FieldParam fieldParam, Object value);

    protected Map<Object, Object> getOptions(FieldParam fieldParam) {
        Map<Object, Object> result = new HashMap<>();
        Map<String, Object> props = fieldParam.getField().getProps();
        if(MapUtils.isEmpty(props)){
            return result;
        }

        Object selectObj = props.get(fieldParam.getField().getType());
        if(! (selectObj instanceof Map)){
            return result;
        }

        Map<?, ?> select = (Map<?, ?>) selectObj;
        Object optionsObj = select.get("options");
        if(! (optionsObj instanceof List)){
            return result;
        }

        List<?> options = (List<?>) optionsObj;
        for(Object optionObj: options){
            if((!(optionObj instanceof Map))){
                continue;
            }
            Map<?, ?> option = (Map<?, ?>) optionObj;
            result.put(option.get("value"), option.get("id"));
        }
        return result;
    }
}
