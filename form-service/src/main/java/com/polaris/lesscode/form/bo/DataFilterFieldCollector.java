package com.polaris.lesscode.form.bo;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.QuoteFieldConfig;
import com.polaris.lesscode.form.internal.sula.RelateTableFieldConfig;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 数据过滤器，字段收集
 */
@Data
public class DataFilterFieldCollector {

    /**
     * 不可见的字段
     */
    private Set<String> invisibleFields;

    /**
     * 子表字段
     */
    private Set<String> subTableFields;

    /**
     * 高级关联字段
     */
    private Map<String, RelateProInfo> relationProInfos;

    /**
     * 关联字段
     */
    private Map<String, RelateTableFieldConfig> relateFieldConfigs;

    /**
     * 引用字段
     */
    private Map<String, QuoteFieldConfig> quoteFieldConfigMap;

    /**
     * 成员字段
     */
    private Set<String> memberFields;

    /**
     * 部门字段
     */
    private Set<String> deptFields;

    // 引用选项字段
    private Map<String,Map<Object, Object>> refOptionFields;
    private Set<String> refDateFields;

    /**
     * 字段字典
     **/
    private Map<String, FieldParam> fieldParamMap;

}
