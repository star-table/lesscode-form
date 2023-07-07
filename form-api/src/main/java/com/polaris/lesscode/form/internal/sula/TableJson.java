package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.List;

@Data
public class TableJson {
    private String layout;
    private String rowKey;
    private List<Column> columns;
    private List<RenderParam> leftActionsRender;
    private List<RenderParam> actionsRender;
    private List<FieldParam> fields;
    private RemoteSourceParam remoteDataSource;

    /**
     * 约束
     **/
    private Constraint constraint;

    /**
     * 关联的组织字段
     **/
    private List<String> baseFields;
}
