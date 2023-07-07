package com.polaris.lesscode.form.internal.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.polaris.lesscode.enums.DescEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 引用方式
 *
 * @author roamer
 * @version v1.0
 * @date 2021/3/1 10:57
 */
public enum QuoteType implements DescEnum<String> {
    /**
     * 原样引用
     **/
    VALUES("VALUES", "原样引用"),
    /**
     * 全计数
     **/
    COUNTALL("COUNTALL", "全计数"),
    ;
    final String code;

    final String desc;

    QuoteType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    private static final Map<String, QuoteType> ENUMS_MAP = new HashMap<>();

    static {
        for (QuoteType t : QuoteType.values()) {
            if (ENUMS_MAP.containsKey(t.code)) {
                throw new RuntimeException("code[" + t.code + "] existed");
            }
            ENUMS_MAP.put(t.code, t);
        }
    }

    @JsonCreator
    public static QuoteType forValue(String code) {
        return ENUMS_MAP.get(code);
    }
}
