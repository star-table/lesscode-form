package com.polaris.lesscode.form.internal.form;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 关联表类型字段
 *
 * @author roamer
 * @version v1.0
 * @date 2021/2/3 16:03
 */
@Data
public class RelateFieldConfig {

    /**
     * 关联AppId
     */
    private Long appId;

    /**
     * 是否关联多条数据 默认否
     */
    private boolean multiple = false;

    /**
     * 示关联表的主要字段名
     **/
    private List<String> displayFields;

}
