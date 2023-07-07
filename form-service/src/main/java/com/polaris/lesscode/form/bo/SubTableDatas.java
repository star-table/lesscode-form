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
public class SubTableDatas {

    /**
     * 子表单key
     **/
    private String key;

    /**
     * 表单数据
     **/
    private List<Map<String, Object>> datas;

    public SubTableDatas(String key, List<Map<String, Object>> datas) {
        this.key = key;
        this.datas = datas;
    }

    public SubTableDatas() {
    }
}
