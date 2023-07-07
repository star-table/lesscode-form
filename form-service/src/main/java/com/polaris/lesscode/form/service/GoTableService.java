package com.polaris.lesscode.form.service;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.gotable.internal.req.CreateSummeryTableRequest;
import com.polaris.lesscode.gotable.internal.req.CreateTableRequest;
import com.polaris.lesscode.gotable.internal.req.ReadSummeryTableIdRequest;
import com.polaris.lesscode.gotable.internal.resp.CreateSummeryTableResp;
import com.polaris.lesscode.gotable.internal.resp.CreateTableResp;
import com.polaris.lesscode.gotable.internal.resp.ReadSummeryTableIdResp;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;

import java.util.List;
import java.util.Map;

public interface GoTableService {
    Map<String, FieldParam> readFields(Long tableId, Long orgId, Long userId);
    Map<Long, Map<String, FieldParam>> readTablesFields(List<Long> tableIds, Long orgId, Long userId);
    TableSchemas readSchema(Long tableId, Long orgId, Long userId, boolean isNeedRefColumn);
    TableSchemas readSchemaByAppId(Long appId, Long orgId, Long userId);
    List<TableSchemas> readSchemas(List<Long> tableId, Long orgId, Long userId, boolean isNeedRefColumn);
    CreateTableResp create(CreateTableRequest req, Long orgId, Long userId);
    CreateSummeryTableResp createSummery(CreateSummeryTableRequest req, Long orgId, Long userId);
    ReadSummeryTableIdResp readSummeryTableId(ReadSummeryTableIdRequest req, Long orgId, Long userId);
}
