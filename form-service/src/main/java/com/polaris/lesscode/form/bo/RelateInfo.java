package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.Objects;

/**
 * 普通关联信息
 *
 * @author Nico
 * @date 2021/2/20 17:15
 */
@Data
public class RelateInfo {

    private Long appId;

    private String fieldKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelateInfo that = (RelateInfo) o;
        return Objects.equals(fieldKey, that.fieldKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldKey);
    }
}
