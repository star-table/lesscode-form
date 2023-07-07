package com.polaris.lesscode.form.internal.form;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.RefSetting;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FormRefDsl {
    public static Query getRefDSL(Long orgId, Query subQuery, Map<String, FieldParam> fieldParamMap, Map<Long,
            Map<String, FieldParam>> tablesFields, boolean isAggNoLimit) {

        String tableName = subQuery.getFrom().get(0).getSchema();
        List<String> originColumns = subQuery.getColumns();
        int filterColumnCount = 0;
        if (!CollectionUtils.isEmpty(subQuery.getColumns())) {
            filterColumnCount = subQuery.getColumns().size();
        }
        int finalFilterColumnCount = filterColumnCount;
        List<String> refFieldNames = new ArrayList<>();
        fieldParamMap.forEach((fieldName, fieldParam) -> {
            RefSetting setting = fieldParam.getField().getRefSetting();
            if (setting != null && checkIsConditionRef(fieldParam.getField().getType())) {
                if (finalFilterColumnCount == 0 || checkIsInFieldNameList(subQuery.getColumns(), fieldName)) {
                    refFieldNames.add(fieldName);
                }
            }
        });

        List<String> lastFilterColumns = new ArrayList<>(SqlUtil.notJsonField);
//        lastFilterColumns.add("id");
        // 不传就是拿全部，或者里面包含data
        if (originColumns == null) {
            lastFilterColumns.add("data");
        } else {
            AtomicBoolean hasData = new AtomicBoolean(false);
            originColumns.forEach(item -> {
                if (item.contains("data") && !checkIsInFieldNameList(refFieldNames, item)) {
                    hasData.set(true);
                }
            });
            if (hasData.get()) {
                lastFilterColumns.add("data");
            }
        }

        // 默认子查询都查出所有的列，在外部限制
        subQuery.setColumns(null);

        AtomicReference<Integer> i = new AtomicReference<>(1);
        AtomicReference<Query> finalQuery = new AtomicReference<>(subQuery);
        refFieldNames.forEach(fieldName -> {
            FieldParam fieldParam = fieldParamMap.get(fieldName);
            RefSetting setting = fieldParam.getField().getRefSetting();

            String rightAlias = "t" + i;
            String leftAlias = "t" + (i.get() + 1);
            List<Condition> onConditions = new ArrayList<>();
            for (Condition condition1 : setting.getCondition().getConds()) {
                ColumnInfo fromColumn = new ColumnInfo(rightAlias, condition1.getColumn(), ColumnInfo.STRING, null);
                ColumnInfo toColumn = new ColumnInfo(leftAlias, condition1.getValue().toString(), ColumnInfo.STRING, null);
                if (tablesFields.get(setting.getTableId()) != null) {
                    FieldParam.setColumnInfo(tablesFields.get(setting.getTableId()).get(fromColumn.getName()), fromColumn);
                }
                FieldParam.setColumnInfo(fieldParamMap.get(condition1.getValue().toString()), toColumn);

                onConditions.add(Conditions.joinCondition(fromColumn, toColumn, condition1.getType()));
            }

            boolean isRefArrayColumn = false;
            if (tablesFields.get(setting.getTableId()) != null) {
                FieldParam refColumnFieldParam = tablesFields.get(setting.getTableId()).get(setting.getColumnId());
                if (refColumnFieldParam != null) {
                    isRefArrayColumn = refColumnFieldParam.checkIsArrayColumn();
                }
            }

            List<String> columns = getFilterColumns(lastFilterColumns, leftAlias);
            columns.add(Agg.aggJsonColumn(setting.getAggFunc(), rightAlias, setting.getColumnId(), "\""+fieldName+"\"", isRefArrayColumn, isAggNoLimit));

            Table table = new Table(finalQuery.get(), leftAlias);
            table.leftJoin(new Table(null, tableName, rightAlias));
            List<Condition> defaultConditions = getTableDefaultConditions(orgId, setting.getTableId(), rightAlias);
            if (setting.getCondition().getType().equals(Conditions.OR)) {
                defaultConditions.add(Conditions.or(onConditions));
                table.on(Conditions.and(defaultConditions));
            } else {
                onConditions.addAll(defaultConditions);
                table.on(Conditions.and(onConditions));
            }

            finalQuery.set(Query.select(columns).from(table).group(SqlUtil.wrapperAliasColumn(leftAlias, "id")));

            i.set(i.get() + 2);
            lastFilterColumns.add(fieldName);
        });

//        if (originColumns != null && originColumns.size() > 0) {
//            // 移除带data的引用列，因为已经改为正常名字了
//            originColumns.removeIf(item -> {
//                if (item.equals("id") || item.equals("data")) {
//                    return true;
//                }
//                for (String str : refFieldNames) {
//                    if (item.contains(str)) {
//                        return true;
//                    }
//                }
//                return false;
//            });
//            if (originColumns.size() > 0) {
//                originColumns.add("id");
//                originColumns.addAll(refFieldNames);
//                return Query.select(originColumns).from(new Table(finalQuery.get(), "tx"));
//            }
//        }

        return finalQuery.get();
    }

    private static List<String> getFilterColumns(List<String> filterColumns, String alias) {
        List<String> newColumns = new ArrayList<>();
        for (String column : filterColumns) {
            if (!column.equals("id")) {
                if (!column.contains("\"") && !column.contains("'")) {
                    column = "\"" + column + "\"";
                }
                newColumns.add("first(" + alias + "." + column + ") as " + column);
            } else {
                newColumns.add(SqlUtil.wrapperAliasColumn(alias, column));
            }
        }

        return newColumns;
    }

    public static List<Condition> getTableDefaultConditions(Long orgId, Long tableId, String alias) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(Conditions.equal(SqlUtil.wrapperAliasJsonColumn(alias,SqlUtil.ORG_ID), orgId));
        conditions.add(Conditions.equal(SqlUtil.wrapperAliasJsonColumn(alias,SqlUtil.RECYCLE_FLAG), CommonConsts.FALSE));
        conditions.add(Conditions.equal(SqlUtil.wrapperAliasJsonColumn(alias,SqlUtil.TABLE_ID), tableId.toString()));

        return conditions;
    }

    public static List<String> getFinalFilterColumns(List<String> filterColumns) {
        List<String> newColumns = new ArrayList<>();
        filterColumns.forEach(item -> {
            if (item.contains("as")) {
                String[] fs = item.split("as");
                newColumns.add(fs[1].trim());
            } else {
                String[] fs = item.split(" ");
                newColumns.add(fs[fs.length - 1].trim());
            }
        });

        return newColumns;
    }

    public static boolean checkIsInFieldNameList(List<String> filterColumns, String fieldName) {
        if (filterColumns == null) {
            return false;
        }
        for (String column : filterColumns) {
           if (column.contains(fieldName)) {
               return true;
           }
        }

       return false;
    }

    public static boolean checkIsConditionRef(String type) {
        return type.equals(FieldTypeEnums.CONDITION_REF.getFormFieldType()) || type.equals(FieldTypeEnums.CONDITION_REF.getCode().toString());
    }
}
