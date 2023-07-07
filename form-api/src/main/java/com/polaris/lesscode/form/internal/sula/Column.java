package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.List;

/**
 * @Author Liu.B.J
 */
@Data
public class Column {

    private String key;
    private String title;
    private String enTitle;
    private String aliasTitle;
    private List<RenderParam> render;
    private Field field;
    private List<RuleParam> rules;
    private Boolean isSys = false;
    private Boolean isOrg = false;
    private Boolean writable = true;
    private Boolean editable = true;

    /**
     * 是否唯一
     **/
    private Boolean unique = false;
    // 子表单
    private List<Children> children;

    public Column(String key, String title, Field field, Boolean isSys){
        this.key = key;
        this.title = title;
        this.field = field;
        this.isSys = isSys;
    }

    public Column(FieldParam fieldParam){
        this.key = fieldParam.getName();
        this.title = fieldParam.getLabel();
        this.enTitle = fieldParam.getEnLabel();
        this.aliasTitle = fieldParam.getAliasLabel();
        this.field = fieldParam.getField();
        this.isSys = fieldParam.getIsSys();
        this.writable = fieldParam.getWritable();
        this.editable = fieldParam.getEditable();
    }

    public Column(){

    }
}
