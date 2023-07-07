package com.polaris.lesscode.form.service;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.dsl.Set;
import com.polaris.lesscode.form.bo.Relating;
import com.polaris.lesscode.form.bo.TableDatas;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.util.DateTimeFormatterUtils;
import com.polaris.lesscode.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.polaris.lesscode.form.bo.Relating.LINK_TO;
import static com.polaris.lesscode.form.bo.Relating.LINK_FROM;

@Slf4j
@Service
public class DataRelatingService {
    @Autowired
    private DataFilterService dataFilterService;

    // 添加数据触发关联的数据关联类型数据更新
    public void handleRelatingDataAdd(Long orgId,List<Executor> executors, String tableName, TableDatas tableDatas, Long sourceDataId, Long userId, Map<String, FieldParam> fieldParams) {
        tableDatas.getData().forEach((key, item) -> {
            if (fieldParams.containsKey(key) && FormFieldConstant.NEED_DEAL_RELATING_TYPE.contains(fieldParams.get(key).getField().getType())) {
                _handleRelatingDataAdd(orgId, executors, tableName, tableDatas, key, sourceDataId, userId);
            }
        });
    }

    public void _handleRelatingDataAdd(Long orgId,List<Executor> executors, String tableName, TableDatas tableDatas, String fieldName, Long sourceDataId, Long userId) {
        Object value = tableDatas.getData().get(fieldName);
        if (value == null) return;

        Table table = new Table(tableName);
        if (value instanceof Map) {
            Map valueMap = (Map)value;
            if (valueMap.containsKey(LINK_TO)) {
                Object linkTo = valueMap.get(LINK_TO);
                log.info("[Relating] [DataAdd] LINK_TO {} SourceDataId:{} LinkTo:{}", tableName, sourceDataId, linkTo);
                if (linkTo instanceof Collection) {
                    Set uniqueIds = new Set();
                    for (Object o: (Collection)linkTo) {
                        if (sourceDataId != Long.valueOf((String) o)) {
                            _executeRelatingDel(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, (String) o, userId);
                            _executeRelatingAdd(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, (String) o, userId);
                        }
                    }
                }
            }
            if (valueMap.containsKey(LINK_FROM)) {
                Object linkFrom = valueMap.get(LINK_FROM);
                log.info("[[Relating] [DataAdd] LINK_FROM {} SourceDataId:{} LinkFrom:{}", tableName, sourceDataId, linkFrom);
                if (linkFrom instanceof Collection) {
                    for (Object o: (Collection)linkFrom) {
                        if (sourceDataId != Long.valueOf((String) o)) {
                            _executeRelatingDel(orgId, executors, table, fieldName, LINK_TO, sourceDataId, (String) o, userId);
                            _executeRelatingAdd(orgId, executors, table, fieldName, LINK_TO, sourceDataId, (String) o, userId);
                        }
                    }
                }
            }
        }
    }

    public void handleRelatingDataUpdate(Long orgId,List<Executor> executors, String tableName, Map<String, Object> updateData,
                                         Long userId,Map<String, Object> oldData, Map<String, FieldParam> fieldParams) {
        // 一个组织都是在同一张汇总表里
        Table table = new Table(tableName);
        Long sourceDataId = Long.valueOf(oldData.get(FormFieldConstant.ISSUE_ID).toString());

        // 如果是放到回收站
        if (updateData.containsKey(FormFieldConstant.RECYCLE_FLAG)) {
            fieldParams.forEach((fieldName, fieldParam) -> {
                if (FormFieldConstant.NEED_DEAL_RELATING_TYPE.contains(fieldParam.getField().getType())) {
                    _handleRelatingDataRecycle(orgId, executors, table, updateData, oldData, fieldName, sourceDataId, userId);
                }
            });
        }

        // 如果是修改了表关联数据
        updateData.forEach((key, item) -> {
            if (fieldParams.containsKey(key) && FormFieldConstant.NEED_DEAL_RELATING_TYPE.contains(fieldParams.get(key).getField().getType())) {
                _handleRelatingDataUpdate(orgId, executors, table, updateData, oldData, key, sourceDataId, userId);
            }
        });
    }

    public void _handleRelatingDataRecycle(Long orgId, List<Executor> executors, Table table, Map<String, Object> updateData, Map<String, Object> oldData, String fieldName, Long sourceDataId, Long userId) {
        if (!updateData.containsKey(FormFieldConstant.RECYCLE_FLAG)) return;
        if (!oldData.containsKey(fieldName)) return;

        Relating oldRelating = new Relating(oldData.get(fieldName));

        log.info("[Relating] [DataRecycle] {} SourceDataId:{} RecycleFlag:{} {} old:{}", table.getSchema(), sourceDataId, updateData.get(FormFieldConstant.RECYCLE_FLAG), fieldName, oldRelating);

        // 处理LinkTo
        List<String> addList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        if (Objects.equals((Integer) updateData.get(FormFieldConstant.RECYCLE_FLAG), CommonConsts.TRUE)) {
            delList = oldRelating.getLinkTo();
        } else {
            addList = oldRelating.getLinkTo();
        }
        for (String i : addList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingAdd(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, i, userId);
            }
        }
        for (String i : delList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingDel(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, i, userId);
            }
        }

        // 处理LinkFrom
        addList = new ArrayList<>();
        delList = new ArrayList<>();
        if ((Integer)updateData.get(FormFieldConstant.RECYCLE_FLAG) == CommonConsts.TRUE) {
            delList = oldRelating.getLinkFrom();
        } else {
            addList = oldRelating.getLinkFrom();
        }
        for (String i : addList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingAdd(orgId, executors, table, fieldName, LINK_TO, sourceDataId, i, userId);
            }
        }
        for (String i : delList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingDel(orgId, executors, table, fieldName, LINK_TO, sourceDataId, i, userId);
            }
        }
    }

    public void _handleRelatingDataUpdate(Long orgId,List<Executor> executors, Table table, Map<String, Object> updateData, Map<String, Object> oldData, String fieldName, Long sourceDataId, Long userId) {
        if (!updateData.containsKey(fieldName)) return;

        Relating updateRelating = new Relating(updateData.get(fieldName));
        Relating oldRelating = new Relating(oldData.get(fieldName));

        log.info("[Relating] [DataUpdate] {} SourceDataId:{} {} old:{} new:{}", table.getSchema(), sourceDataId, fieldName, oldRelating, updateRelating);

        // 处理LinkTo更新
        List<String> addList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        Relating.compare(updateRelating.getLinkTo(), oldRelating.getLinkTo(), addList, delList);
        for (String i : addList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingAdd(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, i, userId);
            }
        }
        for (String i : delList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingDel(orgId, executors, table, fieldName, LINK_FROM, sourceDataId, i, userId);
            }
        }

        // 处理LinkFrom更新
        addList = new ArrayList<>();
        delList = new ArrayList<>();
        Relating.compare(updateRelating.getLinkFrom(), oldRelating.getLinkFrom(), addList, delList);
        for (String i : addList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingAdd(orgId, executors, table, fieldName, LINK_TO, sourceDataId, i, userId);
            }
        }
        for (String i : delList) {
            if (sourceDataId != Long.valueOf(i)) {
                _executeRelatingDel(orgId, executors, table, fieldName, LINK_TO, sourceDataId, i, userId);
            }
        }
    }

    private void _executeRelatingAdd(Long orgId, List<Executor> executors, Table table, String fieldName, String subFieldName, Long sourceDataId, String dataId, Long userId) {
        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());

        // 更新关联
        executors.add(Executor.
                update(table).
                set(Sets.setJsonBArray("data." + fieldName + "." + subFieldName, String.valueOf(sourceDataId), Set.ACTION_JSON_ARRAY_ADD_ITEM)).
                where(getIdCondition(Long.valueOf(dataId), orgId)));

        // 更新时间 更新人等
        executors.add(Executor.update(table).set(Sets.set("\""+FormFieldConstant.UPDATOR + "\"", userId)).set(Sets.set("\""+FormFieldConstant.UPDATE_TIME + "\"", dtNow)).where(getIdCondition(Long.valueOf(dataId), orgId)));
    }

    private void _executeRelatingDel(Long orgId, List<Executor> executors, Table table, String fieldName, String subFieldName, Long sourceDataId, String dataId, Long userId) {
        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());

        // 更新关联
        executors.add(Executor.
                update(table).
                set(Sets.setJsonBArray("data." + fieldName + "." + subFieldName, String.valueOf(sourceDataId), Set.ACTION_JSON_ARRAY_DEL_ITEM)).
                where(getIdCondition(Long.valueOf(dataId), orgId)));

        // 更新时间 更新人等
        executors.add(Executor.update(table).set(Sets.set("\""+FormFieldConstant.UPDATOR + "\"", userId)).set(Sets.set("\""+FormFieldConstant.UPDATE_TIME + "\"", dtNow)).where(getIdCondition(Long.valueOf(dataId), orgId)));
    }

    private Condition getIdCondition(Long issueId, Long orgId) {
        return Conditions.and(
            Conditions.equal("\"issueId\"", issueId),
            Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)
        );
    }
}
