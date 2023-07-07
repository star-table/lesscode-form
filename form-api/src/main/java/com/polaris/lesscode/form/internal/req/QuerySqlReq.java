package com.polaris.lesscode.form.internal.req;

import lombok.Data;

@Data
public class QuerySqlReq {
    Long orgId;
    Long userId;
    String query;
    String fieldParams;
    Long summaryTableId;
}
