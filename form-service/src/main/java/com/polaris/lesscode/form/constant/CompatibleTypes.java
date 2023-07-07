package com.polaris.lesscode.form.constant;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;

import java.util.*;

public class CompatibleTypes {

    private static final Map<FieldTypeEnums, List<FieldTypeEnums>> COMPATIBLE_TYPES = new HashMap<>();

    static {
        COMPATIBLE_TYPES.put(FieldTypeEnums.SINGLE_TEXT, Arrays.asList(FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.MUL_TEXT, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.DATE, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.EMAIL, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.PHONE, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.NUMBER, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.STATUS, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.AMOUNT, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.DOCUMENT, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.LINK, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.RICH_TEXT, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.IMAGE, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.AUTO_NUMBER, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
//        COMPATIBLE_TYPES.put(FieldTypeEnums.IP, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.RANGE_NUMBER, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
        COMPATIBLE_TYPES.put(FieldTypeEnums.IDENTITY_CARD, Arrays.asList(FieldTypeEnums.SINGLE_TEXT, FieldTypeEnums.MUL_TEXT, FieldTypeEnums.RICH_TEXT));
    }

    public static boolean isCompatible(FieldTypeEnums source, FieldTypeEnums target){
        if (source == null || target == null){
            return false;
        }
        List<FieldTypeEnums> fieldTypeEnums = COMPATIBLE_TYPES.get(source);
        if (fieldTypeEnums == null){
            return false;
        }
        return fieldTypeEnums.contains(target);
    }

}
