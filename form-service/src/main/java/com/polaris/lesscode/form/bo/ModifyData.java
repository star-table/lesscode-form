package com.polaris.lesscode.form.bo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class ModifyData {

    private Map<String, Object> data;

    private String field;

    private Object value;

    public ModifyData(Map<String, Object> data, String field, Object value) {
        this.data = data;
        this.field = field;
        this.value = value;
    }

    public void apply(){
        data.put(field, value);
        log.info("ModifyData {} set {} to {}", JSON.toJSONString(data), field, value);
    }
}
