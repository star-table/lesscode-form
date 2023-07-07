package com.polaris.lesscode.form.internal.sula;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
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
public class RelateTableFieldConfig {

    /**
     * 显示关联表的主要字段名
     * 为null或者""，则取第一个字段
     */
    private String display_field;

    /**
     * 具体引用的哪几列，全传的话会展示前4列
     *
     * @Author Nico
     * @Date 2021/3/12 12:02
     **/
    private List<FieldParam> display_columns;

    private String formName;

    /**
     * 关联AppId
     */
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long appId;

    @ApiModelProperty("columns")
    private List<FieldParam> columns;

    /**
     * 是否关联多条数据 默认否
     */
    private Boolean multiple;

    public RelateTableFieldConfig() {
        multiple = false;
    }

    public Boolean getMultiple() {
        return !Objects.isNull(multiple) && multiple;
    }
}
