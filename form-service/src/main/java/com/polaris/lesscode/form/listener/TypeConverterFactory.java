package com.polaris.lesscode.form.listener;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;

import java.util.HashMap;
import java.util.Map;

public class TypeConverterFactory {

    private static final Map<FieldTypeEnums, TypeConverter> converts = new HashMap<>();

    static {
        converts.put(FieldTypeEnums.SINGLE_TEXT, new StringTypeConverter());
        converts.put(FieldTypeEnums.MUL_TEXT, new StringTypeConverter());
        converts.put(FieldTypeEnums.DATE, new StringTypeConverter());
        converts.put(FieldTypeEnums.EMAIL, new StringTypeConverter());
        converts.put(FieldTypeEnums.PHONE, new StringTypeConverter());
        converts.put(FieldTypeEnums.NUMBER, new NumberTypeConverter());

        converts.put(FieldTypeEnums.SUBFORM, new StringTypeConverter());    //子表单
        converts.put(FieldTypeEnums.SELECT, new SelectTypeConverter());     //单选
        converts.put(FieldTypeEnums.GROUP_SELECT, new SelectTypeConverter());     //单选
        converts.put(FieldTypeEnums.STATUS, new NumberTypeConverter());
        converts.put(FieldTypeEnums.AMOUNT, new NumberTypeConverter());
        converts.put(FieldTypeEnums.MULTISELECT, new MultipartSelectTypeConverter());    //多选

        converts.put(FieldTypeEnums.LINK, new StringTypeConverter());
        converts.put(FieldTypeEnums.RICH_TEXT, new StringTypeConverter());
        converts.put(FieldTypeEnums.IMAGE, new StringTypeConverter());
//        converts.put(FieldTypeEnums.IP, new StringTypeConverter());
        converts.put(FieldTypeEnums.IDENTITY_CARD, new StringTypeConverter());

        // 人员
//        converts.put(FieldTypeEnums.TREE_SELECT, new StringTypeConverter());
//        converts.put(FieldTypeEnums.USER, new StringTypeConverter());
//        converts.put(FieldTypeEnums.DEPT, new StringTypeConverter());

    }

    public static boolean isSupported(FieldTypeEnums fieldType){
        return converts.containsKey(fieldType);
    }

    public static Object parse(FieldTypeEnums fieldType, FieldParam fieldParam, Object value){
        if (isSupported(fieldType)){
            return converts.get(fieldType).parse(fieldParam, value);
        }
        return value;
    }

}
