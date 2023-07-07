package com.polaris.lesscode.form.bo;

import com.polaris.lesscode.dc.internal.dsl.SqlUtil;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.FormJson;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务表单对象
 *
 * @author Nico
 * @date 2021/3/4 16:00
 */
@Data
public class BizForm {

    private Long orgId;

    private Long appId;

    private Long tableId;

    private String appName;

    private Long formId;

    private Long extendsId;

    private Long extendsFormId;

    private FormJson config;

    private Long projectId;

    private Integer appType;

    private boolean isWorkflow;

    private Map<String, FieldParam> extendsFieldParams;

    private Map<String, FieldParam> fieldParams;

    private Map<Long, Map<String, FieldParam>> relateTablesFieldParams;

    public boolean hasExtends(){
        return extendsId != null && extendsId > 0;
    }

    public String getTableName(){
        return FormConstant.TABLE;
//        return SqlUtil.wrapperTableName(orgId, extendsFormId == null ? formId : extendsFormId);
    }

    public List<FieldParam> getFieldList(){
        if(fieldParams != null){
            return new ArrayList<>(fieldParams.values());
        }
        return null;
    }
}
