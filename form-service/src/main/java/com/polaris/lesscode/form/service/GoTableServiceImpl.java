package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.gotable.internal.api.GoTableApi;
import com.polaris.lesscode.gotable.internal.req.*;
import com.polaris.lesscode.gotable.internal.resp.CreateSummeryTableResp;
import com.polaris.lesscode.gotable.internal.resp.CreateTableResp;
import com.polaris.lesscode.gotable.internal.resp.ReadSummeryTableIdResp;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoTableServiceImpl implements GoTableService {
    @Autowired
    private GoTableApi goTableApi;

    public Map<String, FieldParam> readFields(Long tableId, Long orgId, Long userId) {
        Map<String, FieldParam> fieldsMap = new HashMap<>();
        List<Long> tableIds = new ArrayList<>();
        tableIds.add(tableId);
        List<TableSchemas> list = readSchemas(tableIds, orgId,userId,false);
        if (list.size() > 0) {
            list.get(0).getColumns().forEach(object ->{
                FieldParam fp  = JSON.toJavaObject(object,FieldParam.class);
                fieldsMap.put(fp.getName(), fp);
            });
        }

        return fieldsMap;
    }

    public Map<Long, Map<String, FieldParam>> readTablesFields(List<Long> tableIds, Long orgId, Long userId) {
        Map<Long, Map<String, FieldParam>> tablesFieldsMap = new HashMap<>();
        List<TableSchemas> list = readSchemas(tableIds, orgId,userId, false);
        if (list.size() > 0) {
            list.forEach(table -> {
                Map<String, FieldParam> fieldsMap = new HashMap<>();
                table.getColumns().forEach(object -> {
                    FieldParam fp  = JSON.toJavaObject(object,FieldParam.class);
                    fieldsMap.put(fp.getName(), fp);
                });
                tablesFieldsMap.put(table.getTableId(), fieldsMap);
            });
        }

        return tablesFieldsMap;
    }


    public TableSchemas readSchema(Long tableId, Long orgId, Long userId, boolean isNeedRefColumn) {
        List<Long> tableIds = new ArrayList<>();
        tableIds.add(tableId);
        List<TableSchemas> list = readSchemas(tableIds, orgId,userId, isNeedRefColumn);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    // 理论上来说form只有汇总表的时候用到，一般都是通过tableId来获取
    public TableSchemas readSchemaByAppId(Long appId, Long orgId, Long userId) {
        List<TableSchemas> list = goTableApi.readSchemasByAppId(new ReadTableSchemasByAppIdRequest(appId,true), orgId.toString(),userId.toString()).getTables();
        if (list != null && list.size() >= 1) {
            return list.get(0);
        }

        return null;
    }

    public List<TableSchemas> readSchemas(List<Long> tableIds, Long orgId, Long userId, boolean isNeedRefColumn) {
        return goTableApi.readSchemas(new ReadTableSchemasRequest(tableIds,true, isNeedRefColumn), orgId.toString(),userId.toString()).getTables();
    }

    public CreateTableResp create(CreateTableRequest req, Long orgId, Long userId) {
        return goTableApi.create(req,orgId.toString(),userId.toString());
    }

    public  CreateSummeryTableResp createSummery(CreateSummeryTableRequest req, Long orgId, Long userId){
        return goTableApi.createSummery(req,orgId.toString(),userId.toString());
    }

    public ReadSummeryTableIdResp readSummeryTableId(ReadSummeryTableIdRequest req, Long orgId, Long userId){
        return goTableApi.readSummeryTableId(req,orgId.toString(),userId.toString());
    }
}
