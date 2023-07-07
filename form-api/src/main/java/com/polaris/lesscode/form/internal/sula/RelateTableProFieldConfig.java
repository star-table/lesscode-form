package com.polaris.lesscode.form.internal.sula;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

import java.util.Objects;

/**
 * 高级关联表
 *
 * @author wangshichang
 */
@Data
public class RelateTableProFieldConfig {

    /**
     * 是否关联多条数据 默认否
     */
    private Boolean multiple;

    /**
     * 关联表的appId
     */
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long appId;

    /**
     * 当前表的条件列
     */
    private FieldParam currentCond;

    /**
     * 被关联表的条件列
     */
    private FieldParam linkCond;

    /**
     * 当前表的当前列
     */
    private FieldParam currentShow;

    /**
     * 被关联表要显示的列
     */
    private FieldParam linkShow;

    /**
     * 值条件
     */
    private Condition valueCond;

    public RelateTableProFieldConfig() {
        multiple = false;
    }

    public Boolean getMultiple() {
        return !Objects.isNull(multiple) && multiple;
    }

    /**
     * 是否是值条件
     *
     * @return
     */
    public boolean asValueCond() {
        return !Objects.isNull(valueCond);
    }

}
