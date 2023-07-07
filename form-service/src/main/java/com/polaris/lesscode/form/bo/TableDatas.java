package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表单数据
 *
 * @author Nico
 * @date 2021/1/27 14:11
 */
@Data
public class TableDatas {

    /**
     * 表单数据
     **/
    private Map<String, Object> data;

    /**
     * 子表单数据
     **/
    private List<SubTableDatas> subDatas;

    public TableDatas(Map<String, Object> data) {
        this.data = data;
    }

    public TableDatas(Map<String, Object> data, List<SubTableDatas> subDatas) {
        this.data = data;
        this.subDatas = subDatas;
    }

    public TableDatas() {
    }
}
