package com.polaris.lesscode.form.internal.sula;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @Author Liu.B.J
 */
@Data
public class TableField {

    /**
     * field id, for later use
     */
    private String id;

    /**
     * field label
     */
    private String name;

    /**
     * field name
     */
    private String key;

    /**
     * field type
     */
    private Integer fieldType;

    /**
     * field options(a big json string for table field)
     */
    private JSONObject fieldOptions;

}
