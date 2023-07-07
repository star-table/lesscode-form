package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Liu.B.J
 */
@Data
public class RelateTableFieldData {

    private Long total;

    private List<Object> data;

    public RelateTableFieldData() {
        data = new ArrayList<>();
        total = 0L;
    }

    public void appendData(Object o) {
        if (Objects.isNull(data)) {
            data = new ArrayList<>();
        }
        data.add(o);
        // todo 暂时的先跟随list长度变化，后期分页查询单独处理
        total = (long) data.size();
    }

    public boolean isEmpty() {
        return Objects.isNull(data) || data.isEmpty();
    }
}
