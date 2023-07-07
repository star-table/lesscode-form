package com.polaris.lesscode.form.internal.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class HeaderUtil {

    public static Map<Object, Object> parseSelectOptions(FieldParam fieldParam){
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
            if (option.get("id") == null || option.get("value") == null) {
                continue;
            }
            result.put(option.get("id"), option.get("value"));
        }
        return result;
    }

    public static List<String> parseSelectOptionsList(FieldParam fieldParam){
        List<String> result = new ArrayList<>();
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
            result.add(String.valueOf(option.get("id")));
        }
        return result;
    }


    public static void appendSelectOptions(FieldParam fieldParam, Object value){
        if (value == null){
            return;
        }
        Map<String, Object> props = fieldParam.getField().getProps();
        if(MapUtils.isEmpty(props)){
            props = new HashMap<>();
            fieldParam.getField().setProps(props);
        }

        Object selectObj = props.get(fieldParam.getField().getType());
        if(! (selectObj instanceof Map)){
            selectObj = new HashMap<>();
            props.put(fieldParam.getField().getType(), selectObj);
        }

        Map<String, Object> select = (Map<String, Object>) selectObj;
        Object optionsObj = select.get("options");
        if(! (optionsObj instanceof List)){
            optionsObj = new ArrayList<>();
            select.put("options", optionsObj);
        }

        List<Object> options = (List<Object>) optionsObj;
        if (options.size() >= 200){
            return;
        }
        for(Object optionObj: options){
            if((!(optionObj instanceof Map))){
                continue;
            }
            Map<?, ?> option = (Map<?, ?>) optionObj;
            if (Objects.equals(option.get("value"), value)){
                return;
            }
        }

        Map<String, Object> newOption = new HashMap<>();
        newOption.put("id", String.valueOf(IdWorker.getId()));
        newOption.put("value", value);
        newOption.put("color", "#377AFF");
        options.add(newOption);
    }

}
