package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.List;

/**
 * @Author: Liu.B.J
 * @Data: 2020/9/11 16:05
 * @Modified:
 */
@Data
public class Children {

    private String key;

    private String title;

    private Boolean unique;

    private Field field;

    private List<RuleParam> rules;

    private Boolean isSys = false;

    private Boolean isOrg = false;

    public Children(String key, String title, Boolean isSys, Field field){
        this.key = key;
        this.title = title;
        this.isSys = isSys;
    }

    public Children(){
    }

}
