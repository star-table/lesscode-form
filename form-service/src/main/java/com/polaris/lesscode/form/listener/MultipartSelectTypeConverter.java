package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.sula.FieldParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MultipartSelectTypeConverter extends TypeConverter{

    @Override
    public Object parse(FieldParam fieldParam, Object value) {
        if (value == null){
            return null;
        }
        String[] values = String.valueOf(value).split(",");
        List<Object> ids = new ArrayList<>();

        Map<Object, Object> options = getOptions(fieldParam);
        for (String v: values){
            Object id = options.get(v);
            if (Objects.nonNull(id)){
                ids.add(id);
            }
        }
        return ids;
    }
}
