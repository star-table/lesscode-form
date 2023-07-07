package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.sula.FieldParam;

import java.util.Map;
import java.util.Objects;

public class SelectTypeConverter extends TypeConverter{

    @Override
    public Object parse(FieldParam fieldParam, Object value) {
        if (value == null){
            return null;
        }
        Map<Object, Object> options = getOptions(fieldParam);
        Object id = options.get(value);
        if (Objects.isNull(id)){
            return null;
        }
        return id;
    }

}
