package com.polaris.lesscode.form.internal.controller;


import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.req.QuerySqlReq;
import com.polaris.lesscode.form.internal.resp.QuerySqlResp;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.service.DataFilterService;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AppApiInternalController implements AppFormApi {
    @Autowired
    private DataFilterService dataFilterService;

    @Override
    public Result<QuerySqlResp> querySql(@Validated @RequestBody QuerySqlReq internalReq) {
        com.polaris.lesscode.form.req.AppValueListReq req = JSON.parseObject(internalReq.getQuery(), com.polaris.lesscode.form.req.AppValueListReq.class);
        if (req.getTableId() ==null || req.getTableId().equals(0L)) {
            req.setTableId(internalReq.getSummaryTableId());
        }
        return Result.ok(dataFilterService.querySql(internalReq, req));
    }
}
