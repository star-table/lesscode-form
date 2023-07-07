package com.polaris.lesscode.form.internal.form;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import lombok.Data;

import java.util.Objects;

/**
 * 高级关联表
 *
 * @author wangshichang
 */
@Data
public class RelateProFieldConfig {

    /**
     * 是否关联多条数据 默认否
     */
    private Boolean multiple;

    /**
     * 关联表的appId
     */
    private Long appId;

    /**
     * 当前表的条件列
     */
    private FormField cond;

    /**
     * 被关联表的条件列
     */
    private FormField dest;

    /**
     * 被关联表要显示的列
     */
    private FieldParam linkShow;

    public RelateProFieldConfig() {
        multiple = false;
    }

    public Boolean getMultiple() {
        return !Objects.isNull(multiple) && multiple;
    }

}
