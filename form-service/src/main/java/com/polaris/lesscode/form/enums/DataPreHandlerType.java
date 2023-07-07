package com.polaris.lesscode.form.enums;

import com.polaris.lesscode.form.functions.DataPreHandler;
import com.polaris.lesscode.form.functions.DomainParseHandler;

/**
 * 数据预处理枚举类型
 *
 * @author Nico
 * @date 2021/2/4 13:42
 */
public enum DataPreHandlerType {

    DOMAIN_PARSE("domain_parse", new DomainParseHandler()),

    ;

    private String code;

    private DataPreHandler handler;

    DataPreHandlerType(String code, DataPreHandler handler) {
        this.code = code;
        this.handler = handler;
    }

    public static DataPreHandlerType parse(String code){
        for(DataPreHandlerType t: values()){
            if(t.code.equals(code)){
                return t;
            }
        }
        return null;
    }

    public Object apply(Object value){
        return this.handler.apply(value);
    }
}
