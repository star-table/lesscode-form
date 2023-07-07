package com.polaris.lesscode.form.internal.sula;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.enums.StorageFieldType;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import lombok.Data;

import java.util.Map;

/**
 * @Author Liu.B.J
 */
@Data
public class Field {

	/**
	 * 1控件类型
	 */
    private String type;

    /**
     * 自定义类型
     **/
    private String customType;

    /**
     * 北极星任务栏id
     */
    private Long projectObjectTypeId;

    /***
     * 2字段类型
     */
    private StorageFieldType dataType;

    /**
     * 3属性
     */
    private Map<String, Object> props;
    
    /**
     * .数据库联动
     */
    private DataRely dataRely;
    
    /**
     * .动态数据关联
     */
    private AsyncData asyncData;

    public RefSetting getRefSetting() {
       if (props != null && props.get(FieldTypeEnums.CONDITION_REF.getFormFieldType()) != null) {
           String str = JSON.toJSONString(props.get(FieldTypeEnums.CONDITION_REF.getFormFieldType()));
           return JSON.parseObject(str, RefSetting.class);
       }

       return null;
    }

    public void setRefSettingAggFunc(String aggFunc) {
        if (props != null && props.get(FieldTypeEnums.CONDITION_REF.getFormFieldType()) != null) {
            Object object = props.get(FieldTypeEnums.CONDITION_REF.getFormFieldType());
            if (object instanceof Map) {
                ((Map) object).put("aggFunc", aggFunc);
            }
        }
    }

    public Field(String type, StorageFieldType dataType){
        this.type = type;
        this.dataType = dataType;
    }

    public Field(){};

}
