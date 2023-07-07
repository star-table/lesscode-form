package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.lang3.StringUtils;

public class NumberTypeConverter extends TypeConverter{

    @Override
    public Object parse(FieldParam fieldParam, Object value) {
        if (value == null){
            return null;
        }
        try{
            return Double.valueOf(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

}
