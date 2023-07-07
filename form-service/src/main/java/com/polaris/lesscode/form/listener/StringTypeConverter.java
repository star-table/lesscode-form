package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.lang3.StringUtils;

public class StringTypeConverter extends TypeConverter{

    @Override
    public Object parse(FieldParam fieldParam, Object value) {
        if (value == null){
            return null;
        }
        return String.valueOf(value);
    }
}
