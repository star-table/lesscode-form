package com.polaris.lesscode.form.service;

import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.mapper.AppFormMapper;
import com.polaris.lesscode.form.util.RedisUtil;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.DateTimeFormatterUtils;
import com.polaris.lesscode.util.DateTimeUtils;
import com.polaris.lesscode.form.req.AppValueUpdateBatchReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataUpdateBatchService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private AppFormMapper appFormMapper;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DataValidateService dataValidateService;

    @Autowired
    private DataUnitService dataUnitService;

    @Autowired
    private DataRelatingService dataRelatingService;

    @Autowired
    private DataFilterService dataFilterService;

    @Autowired
    private DataCollaboratorService dataCollaboratorService;

    @Autowired
    private GoTableService goTableService;

    // 仅限内部调用，权限由调用方保证
    public Boolean updateBatchRaw(Long orgId, Long userId, AppValueUpdateBatchReq req) {
        log.info("updateBatchRaw {} {}", orgId, req);
        if (req.getSets() == null || req.getSets().size() == 0) {
            return false;
        }

        // 基本数据初始化
        Table table = new Table(FormConstant.TABLE);

        List<Executor> executors = new ArrayList<>();
        String now = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());

        // 每个Set分开执行，因为修改的是同一个json字段: data
        req.getSets().forEach(s -> {
            // Set
            Executor executor = Executor.update(table).set(s);
            // Where
            if (req.getCondition() != null) {
                executor.where(req.getCondition());
            }
            executors.add(executor);
        });

        // 更新updator, updateTime
        Executor executor = Executor.update(table);
        executor.set(Sets.set(FormFieldConstant.UPDATOR, userId));
        // Where
        if (req.getCondition() != null) {
            executor.where(req.getCondition());
        }
        executors.add(executor);

        dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors);
        return true;
    }
}
