package com.polaris.lesscode.form.constant;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.enums.StorageFieldType;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.Children;
import com.polaris.lesscode.form.internal.sula.Column;
import com.polaris.lesscode.form.internal.sula.Field;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.form.resp.FormFieldResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author: Liu.B.J
 * @date: 2021/1/27 19:40
 * @description:
 */
public interface CommonField {

    String CREATE_TIME = "createTime";
    String CREATOR = "creator";
    String UPDATE_TIME = "updateTime";
    String UPDATOR = "updator";
    String STATUS = "status";
    String RECYCLE_FLAG = "recycleFlag";
    String RECYCLE_TIME = "recycleTime";

    Map<String, String> commonFieldNameMap = new HashMap<String, String>(){
        {
            put(CREATE_TIME, "创建时间");
            put(CREATOR, "创建人");
            put(UPDATE_TIME, "更新时间");
            put(UPDATOR, "更新人");
            put(STATUS, "启用状态");
            put(RECYCLE_FLAG, "回收状态");
            put(RECYCLE_TIME, "回收时间");
        }
    };

    Map<String, String> commonFieldTypeMap = new HashMap<String, String>(){
        {
            put(CREATE_TIME, FieldTypeEnums.DATE.getFormFieldType());
            put(CREATOR, FieldTypeEnums.USER.getFormFieldType());
            put(UPDATE_TIME, FieldTypeEnums.DATE.getFormFieldType());
            put(UPDATOR, FieldTypeEnums.USER.getFormFieldType());
            put(STATUS, FieldTypeEnums.STATUS.getFormFieldType());
        }
    };

    Field createTimeField = new Field(commonFieldTypeMap.get(CREATE_TIME), StorageFieldType.DATE);
    Field creatorField = new Field(commonFieldTypeMap.get(CREATOR), StorageFieldType.LONG);
    Field updateTimeField = new Field(commonFieldTypeMap.get(UPDATE_TIME), StorageFieldType.DATE);
    Field updatorField = new Field(commonFieldTypeMap.get(UPDATOR), StorageFieldType.LONG);
    Field statusField = new Field(commonFieldTypeMap.get(STATUS), StorageFieldType.DOUBLE);

    Column createTimeColumn = new Column(CREATE_TIME, commonFieldNameMap.get(CREATE_TIME), createTimeField, true);
    Column creatorColumn = new Column(CREATOR, commonFieldNameMap.get(CREATOR), creatorField, true);
    Column updateTimeColumn = new Column(UPDATE_TIME, commonFieldNameMap.get(UPDATE_TIME), updateTimeField, true);
    Column updatorColumn = new Column(UPDATOR, commonFieldNameMap.get(UPDATOR), updatorField, true);
    Column statusColumn = new Column(STATUS, commonFieldNameMap.get(STATUS), statusField, true);
    List<Column> commonColumns = new ArrayList<Column>(){
        {
            add(createTimeColumn);
            add(creatorColumn);
            add(updateTimeColumn);
            add(updatorColumn);
            add(statusColumn);
        }
    };

    Children createTimeChildren = new Children(CREATE_TIME, commonFieldNameMap.get(CREATE_TIME), true, createTimeField);
    Children creatorChildren = new Children(CREATOR, commonFieldNameMap.get(CREATOR), true, creatorField);
    Children updateTimeChildren = new Children(UPDATE_TIME, commonFieldNameMap.get(UPDATE_TIME), true, updateTimeField);
    Children updatorChildren = new Children(UPDATOR, commonFieldNameMap.get(UPDATOR), true, updatorField);
    Children statusChildren = new Children(STATUS, commonFieldNameMap.get(STATUS), true, statusField);
    List<Children> commonChildrens = new ArrayList<Children>(){
        {
            add(createTimeChildren);
            add(creatorChildren);
            add(updateTimeChildren);
            add(updatorChildren);
            add(statusChildren);
        }
    };

    FieldParam createTimeParam = new FieldParam(createTimeField,  CREATE_TIME, commonFieldNameMap.get(CREATE_TIME), true);
    FieldParam creatorParam = new FieldParam(creatorField,  CREATOR, commonFieldNameMap.get(CREATOR), true);
    FieldParam updateTimeParam = new FieldParam(updateTimeField,  UPDATE_TIME, commonFieldNameMap.get(UPDATE_TIME), true);
    FieldParam updatorParam = new FieldParam(updatorField,  UPDATOR, commonFieldNameMap.get(UPDATOR), true);
    FieldParam statusParam = new FieldParam(statusField,  STATUS, commonFieldNameMap.get(STATUS), true);
    List<FieldParam> commonFieldParams = new ArrayList<FieldParam>(){
        {
            add(createTimeParam);
            add(creatorParam);
            add(updateTimeParam);
            add(updatorParam);
            add(statusParam);
        }
    };

    FormFieldResp createTimeResp = new FormFieldResp(CREATE_TIME, commonFieldNameMap.get(CREATE_TIME), commonFieldTypeMap.get(CREATE_TIME));
    FormFieldResp creatorResp = new FormFieldResp(CREATOR, commonFieldNameMap.get(CREATOR), commonFieldTypeMap.get(CREATOR));
    FormFieldResp updateTimeResp = new FormFieldResp(UPDATE_TIME, commonFieldNameMap.get(UPDATE_TIME), commonFieldTypeMap.get(UPDATE_TIME));
    FormFieldResp updatorResp = new FormFieldResp(UPDATOR, commonFieldNameMap.get(UPDATOR), commonFieldTypeMap.get(UPDATOR));
    FormFieldResp statusResp = new FormFieldResp(STATUS, commonFieldNameMap.get(STATUS), commonFieldTypeMap.get(STATUS));
    List<FormFieldResp> commonFormFields = new ArrayList<FormFieldResp>(){
        {
            add(createTimeResp);
            add(creatorResp);
            add(updateTimeResp);
            add(updatorResp);
            add(statusResp);
        }
    };

    static String excludeCommonFields(String config){
        if(! StringUtils.isBlank(config)){
            FormJson currentForm = JSON.parseObject(config, FormJson.class);
            List<FieldParam> fieldParams = currentForm.getFields();
            fieldParams.removeIf(param -> FormFieldConstant.isCommonField(param.getName()));
            for(FieldParam fieldParam: fieldParams){
                if(CollectionUtils.isNotEmpty(fieldParam.getFields())){
                    fieldParam.getFields().removeIf(param -> FormFieldConstant.isCommonField(param.getName()));
                }
            }
            return JSON.toJSONString(currentForm);
        }
        return JSON.toJSONString(new FormJson("create"));
    }

}
