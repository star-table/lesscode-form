package com.polaris.lesscode.form.service;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.enums.DataPreHandlerType;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.req.CopyColumnValueReq;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据单元功能服务，提供简单的功能
 *
 * @author Nico
 * @date 2021-02-21
 */
@Slf4j
@Service
public class DataUnitService {

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private DataValidateService dataValidateService;

    public List<Long> getDataIdsByIssueIds(Long orgId, Long appId, List<Long> issueIds){
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        if (bizForm == null){
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        List<Long> dataIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(issueIds)){
            List<Map<String, Object>> list = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
                    Query.select()
                            .from(new Table(bizForm.getTableName()))
                            .where(Conditions.and(
                                    Conditions.in(SqlUtil.wrapperJsonColumn(FormFieldConstant.ISSUE_ID), issueIds),
                                    Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)
                            ))
            ).getData();
            if (CollectionUtils.isNotEmpty(list)){
                for (Map<String, Object> data: list){
                    Object idObj = data.get(FormFieldConstant.ID);
                    if (idObj != null)
                    dataIds.add(Long.parseLong(idObj.toString()));
                }
            }
        }
        return dataIds;
    }

    /**
     * 处理更新id，例如issueId转id，然后返回要更新的数据列表
     *
     * @Author Nico
     * @Date 2021/6/17 14:14
     **/
    public Map<Long, Map<String, Object>> handleUpdateId(Long orgId, BizForm bizForm, List<Map<String, Object>> datas){
        Map<Long, Map<String, Object>> result = new HashMap<>();
        List<Long> dataIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(datas)){
            List<Long> issueIds = new ArrayList<>();
            for (Map<String, Object> data: datas){
                Object issueIdObj = data.get(FormFieldConstant.ISSUE_ID);
                Object idObj = data.get(FormFieldConstant.ID);
                if (issueIdObj instanceof Number){
                    issueIds.add(((Number) issueIdObj).longValue());
                } else if (idObj != null) {
                    dataIds.add(Long.valueOf((String.valueOf(idObj))));
                }
            }
            if (CollectionUtils.isNotEmpty(issueIds) || CollectionUtils.isNotEmpty(dataIds)){
                Condition condition;
                if (CollectionUtils.isNotEmpty(issueIds)) {
                    condition = Conditions.in(SqlUtil.wrapperJsonColumn(FormFieldConstant.ISSUE_ID), issueIds);
                    condition.setNoPretreat(true);
                } else {
                    condition = Conditions.in(FormFieldConstant.ID, dataIds);
                }
                List<Map<String, Object>> list = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
                        Query.select()
                                .from(new Table(bizForm.getTableName()))
                                .where(Conditions.and(
                                        condition,
                                        Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)
                                ))
                ).getData();
                if (CollectionUtils.isNotEmpty(list)){
                    Map<Object, Object> idMap = new HashMap<>();
                    for (Map<String, Object> data: list){
                        idMap.put(data.get(FormFieldConstant.ISSUE_ID), data.get(FormFieldConstant.ID));
                        result.put(Long.valueOf((String.valueOf(data.get("id")))), data);
                    }
                    for (Map<String, Object> data: datas){
                        if (idMap.containsKey(data.get(FormFieldConstant.ISSUE_ID))){
                            data.put(FormFieldConstant.ID, idMap.get(data.get(FormFieldConstant.ISSUE_ID)));
                            data.remove(FormFieldConstant.ISSUE_ID); // 有id了就不需要issueId了
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 自动补全
     * 
     * @Author Nico
     * @Date 2021/2/26 11:43
     **/
    public List<Map<String, Object>> autoComplete(Long orgId, Long userId, Long appId, String key, String value) {
//        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
//        if (! appAuthorityContext.getAppAuthorityResp().hasFieldReadAuth(key)){
//            throw new BusinessException(ResultCode.FORM_OP_NO_READ);
//        }
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        String keyColumn = SqlUtil.wrapperJsonColumn(key);
        List<Condition> conds = new ArrayList<>();
        conds.add(Conditions.like(keyColumn, "%" + value + "%"));
        conds.add(Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId));
        if (bizForm.hasExtends()){
            conds.add(Conditions.in(SqlUtil.wrapperJsonColumn(FormFieldConstant.APP_ID), Collections.singletonList(appId)));
        }

        Query query = Query.select("distinct " + SqlUtil.wrapperAliasColumn("", keyColumn, "value"))
                .from(new Table(bizForm.getTableName()))
                .where(Conditions.and(conds));
        List<Map<String, Object>> list =  dataCenterProvider.query(DataSourceUtil.getDsId(),
                DataSourceUtil.getDbId(), query).getData();

        if (list != null) {
            List<Map<String, Object>> newList = new ArrayList<>();
            list.forEach(item -> {
                if (item.get("value") != null && item.get("value") instanceof String) {
                    String strValue = StringUtils.trim(item.get("value").toString());
                    if (!strValue.equals("")) {
                        newList.add(item);
                    }
                } else {
                    newList.add(item);
                }
            });

            return newList;
        }

        return null;
    }

    /**
     * 模糊匹配
     *
     * @Author Nico
     * @Date 2021/2/26 11:43
     **/
    public List<Map<String, Object>> fuzzy(Long orgId, Long userId, Long appId, String key, String value) {
//        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
//        if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_READ.getCode()) ||
//                ! appAuthorityContext.getAppAuthorityResp().hasFieldReadAuth(key)){
//            throw new BusinessException(ResultCode.FORM_OP_NO_READ);
//        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        String keyColumn = SqlUtil.wrapperJsonColumn(key);
        Query query = Query.select("id", SqlUtil.wrapperAliasColumn("", keyColumn, "value"))
                .from(new Table(bizForm.getTableName()))
                .where(Conditions.and(Conditions.like(keyColumn, "%" + value + "%")));
        List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(),
                DataSourceUtil.getDbId(), query).getData();
        return datas;
    }

    /**
     * 唯一性校验
     *
     * @Author Nico
     * @Date 2021/2/26 11:46
     **/
    public boolean checkUnique(Long orgId, Long userId, Long appId, Long dataId, String subformKey, String key, Object value) {
        if (value == null) {
            return true;
        }
        if (dataId == null){
            dataId = 0L;
            if (StringUtils.isNotBlank(subformKey)){
                dataId = 1L;
            }
        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        FieldParam targetField = null;
        if(MapUtils.isNotEmpty(bizForm.getFieldParams())){
            if (StringUtils.isNotBlank(subformKey)){
                targetField = bizForm.getFieldParams().get(subformKey);
                if (targetField != null){
                    targetField = com.polaris.lesscode.util.MapUtils.toMap(FieldParam::getName, targetField.getFields()).get(key);
                }
            }else{
                targetField = bizForm.getFieldParams().get(key);
            }
        }
        if (targetField != null) {
            Map<String, Map<String, Map<Long, List<Object>>>> uniques = new HashMap<>();
            String tableName = bizForm.getTableName();
            if (StringUtils.isNotBlank(subformKey)) {
                tableName = SqlUtil.wrapperSubTableName(tableName, subformKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);
            }
            Map<String, Map<Long, List<Object>>> formData = uniques.computeIfAbsent(tableName, k -> new HashMap<>());
            Map<Long, List<Object>> dataMap = formData.computeIfAbsent(targetField.getName(), k -> new HashMap<>());
            List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());

            String uniquePreHandler = targetField.getUniquePreHandler();
            DataPreHandlerType handlerType = null;
            if (StringUtils.isNotBlank(uniquePreHandler)) {
                handlerType = DataPreHandlerType.parse(uniquePreHandler);
            }
            if (handlerType != null) {
                value = handlerType.apply(value);
            }
            dataList.add(value);

            try{
                dataValidateService.validateUnique(uniques, handlerType != null, null);
            }catch(BusinessException e){
                if(Objects.equals(e.getCode(), ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode())){
                    e = new BusinessException(e.getCode(), targetField.getLabel() + "值不能重复");
                }
                throw e;
            }
        }
        return true;
    }

    public Integer copyColumn(Long orgId, Long userId, Long appId, CopyColumnValueReq req) {
        return 0;
//        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
//        if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())
//                && ! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_ISSUE_4_MODIFY_BIND_UNBIND.getCode())){
//            throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE);
//        }
//
//        BizForm bizForm = summaryService.getBizForm(orgId, appId);
//        Executor copyColumnExecutor = Executor.update(new Table(bizForm.getTableName()))
//                .set(Sets.setJsonBWithoutPretreat("data." + req.getDestField(), "COALESCE(" + SqlUtil.wrapperJsonColumn(req.getSourceField()) + ", 'null')"));
//
//        return dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), copyColumnExecutor).getData();
    }

}
