package com.polaris.lesscode.form.bo;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

import java.util.Objects;

/**
 * 高级关联信息
 *
 * @author Nico
 * @date 2021/2/20 15:44
 */
@Data
public class RelateProInfo {

    private Long appId;

    private String linkCondFieldName;

    private String linkShowFieldName;

    private Condition valueCond;

    private String currentCondFieldName;

    private String linkCondFieldType;

    private String linkCondFieldDataType;

    private String currCondFieldType;

    private String currCondFieldDataType;

    private boolean multiple;

    private String fieldKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelateProInfo that = (RelateProInfo) o;
        return Objects.equals(fieldKey, that.fieldKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldKey);
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
