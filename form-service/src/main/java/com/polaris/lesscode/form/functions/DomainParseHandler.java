package com.polaris.lesscode.form.functions;

/**
 * 域名解析处理器
 *
 * @author Nico
 * @date 2021/2/4 11:43
 */
public class DomainParseHandler extends DataPreHandler {
    @Override
    public Object apply(Object value) {
        if(value == null){
            return "";
        }
        String data = String.valueOf(value);
        data = data.trim();

        String[] strs = data.split("://");
        data = strs.length > 1 ? strs[1] : strs[0];

        strs = data.split("/");
        if(strs.length > 1){
            return strs[0] + "/" + strs[1];
        }else{
            return strs[0];
        }
    }
}
