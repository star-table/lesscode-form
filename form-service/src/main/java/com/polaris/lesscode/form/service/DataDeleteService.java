package com.polaris.lesscode.form.service;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.constant.CommonField;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.sula.DeleteConstraint;
import com.polaris.lesscode.form.util.RedisUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.DateTimeFormatterUtils;
import com.polaris.lesscode.util.DateTimeUtils;
import com.polaris.lesscode.workflow.internal.api.WorkflowApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 数据保存服务，提供简单的功能
 *
 * @author Nico
 * @date 2021-02-21 
 */
@Slf4j
@Service
public class DataDeleteService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AppService appService;

    @Autowired
    private DataValidateService dataValidateService;

    @Autowired
    private WorkflowApi workflowApi;

    public Boolean delete(Long orgId, Long userId, Long appId, List<Long> deleteIds) {
        return delete(orgId, userId, appId, deleteIds, false);
    }

    public Boolean delete(Long orgId, Long userId, Long appId, List<Long> deleteIds, boolean isInternal) {
        if (! isInternal){
            AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_DELETE.getCode())){
                throw new BusinessException(ResultCode.FORM_OP_NO_DELETE);
            }
        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId);

        if (!CollectionUtils.isEmpty(deleteIds)) {
            String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
            String tableName = bizForm.getTableName();

            // 处理删除约束
            List<DeleteConstraint> deleteConstraints = null;
            if (bizForm.getConfig().getConstraint() != null){
                deleteConstraints = bizForm.getConfig().getConstraint().getDeleteConstraints();
            }
            List<Executor> deleteExecutors = new ArrayList<>();
            List<Condition> conds = new ArrayList<>();
            List<Table> from = new ArrayList<>();
            conds.add(Conditions.in("id", deleteIds.toArray()));
            conds.add(Conditions.equal(SqlUtil.wrapperJsonColumn("delFlag"), 2));

            Map<String, Object> updated = new HashMap<>();
            updated.put("delFlag", 1);
            updated.put("updator", userId);
            updated.put("updateTime", dtNow);
            Executor deleteExecutor = Executor.update(new Table(tableName))
                    .set(Sets.setJsonB("data", updated))
                    .from(from)
                    .where(Conditions.and(conds));
            deleteExecutors.add(deleteExecutor);

            if(CollectionUtils.isNotEmpty(deleteConstraints)){
                deleteConstraints.forEach(deleteConstraint -> {
                    if(deleteConstraint.getType() == 1){
                        conds.add(deleteConstraint.getCondition());
                        from.add(new Table(deleteConstraint.getTable()));
                    }else if(deleteConstraint.getType() == 2){
                        Executor deleteCascadeExecutor = Executor.update(new Table(deleteConstraint.getTable()))
                                .set(Sets.setJsonB("data", updated))
                                .from(new Table(tableName))
                                .where(Conditions.and(Conditions.in("id", deleteIds.toArray()), deleteConstraint.getCondition()));
                        deleteExecutors.add(deleteCascadeExecutor);
                    }
                });
            }
            // 删除数据对应的工作流ID
            workflowApi.deleteDataWorkflow(deleteIds);
            int[] affects = dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), deleteExecutors).getData();
            return ArrayUtils.isNotEmpty(affects) && affects[0] > 0;
        }
        return false;
    }

    public Boolean deleteSub(Long orgId, Long userId, Long appId, Long dataId, String fieldKey, List<Long> deleteIds) {
        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
//        if (! appAuthorityContext.getAppAuthorityResp().hasFieldWriteAuth(fieldKey)){
//            throw new BusinessException(ResultCode.FORM_OP_NO_DELETE);
//        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId);

        if (!CollectionUtils.isEmpty(deleteIds)) {
            String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
            String subTableName = SqlUtil.wrapperSubTableName(bizForm.getTableName(), fieldKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);

            Executor delExecutor = Executor.update(new Table(subTableName))
                    .set(Sets.setJsonB("data", buildDeleteData(dtNow, userId)))
                    .where(Conditions.and(Conditions.in("id", deleteIds.toArray()), Conditions.equal("parent_id", dataId)));
            return dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), delExecutor).getData() > 0;
        }
        return false;
    }

    private Map<String, Object> buildDeleteData(String deleteTime, Long operatorId){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(CommonField.UPDATOR, operatorId);
        paramMap.put(CommonField.UPDATE_TIME, deleteTime);
        return paramMap;
    }

}
