package com.polaris.lesscode.form.service;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.enums.StorageFieldType;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.bo.CollaboratorRelation;
import com.polaris.lesscode.form.bo.MemberFieldDatas;
import com.polaris.lesscode.form.constant.CommonField;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.enums.DataPreHandlerType;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.mapper.AppFormMapper;
import com.polaris.lesscode.form.req.AppValueListReq;
import com.polaris.lesscode.form.req.AppValueMovingReq;
import com.polaris.lesscode.form.req.AppValueUpdateReq;
import com.polaris.lesscode.form.util.DslUtil;
import com.polaris.lesscode.form.util.MemberFieldTypeUtil;
import com.polaris.lesscode.form.util.RedisUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.util.*;
import com.polaris.lesscode.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * 数据保存服务，提供简单的功能
 *
 * @author Nico
 * @date 2021-02-21 
 */
@Slf4j
@Service
public class DataUpdateService {

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

    public List<Map<String, Object>> update(Long orgId, Long userId, Long appId, AppValueUpdateReq req) {
        return update(orgId, userId, appId, req, false, false);
    }

    // 数据更新
    public List<Map<String, Object>> update(Long orgId, Long userId, Long appId, AppValueUpdateReq req, boolean isInternal, boolean isImport) {
        AppAuthorityContext appAuthorityContext = isInternal ? null : permissionService.appAuth(orgId, appId, userId);
        if (appAuthorityContext != null) {
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())){
                throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE);
            }
        }
        Long tableId = req.getTableId();
        if (tableId == null) {
            tableId = 0L;
        }
        BizForm bizForm = summaryService.getBizForm(orgId, appId, tableId);
        Map<Long, Map<String, Object>> datasMap = dataUnitService.handleUpdateId(orgId, bizForm, req.getForm());

        // 获取字段配置
        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
        fieldParams.putAll(FormFieldConstant.getCommonFields());

        // 基本数据初始化
        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        String mainTableName = bizForm.getTableName();
        Table mainTable = new Table(mainTableName);

        Map<String, Map<String, Map<Long, List<Object>>>> uniques = new HashMap<>();
        Map<String, Map<String, Map<Long, List<Object>>>> preHandleUniques = new HashMap<>();
        Map<Long, String> userIdMap = new HashMap<>();
        Map<Long, String> deptIdMap = new HashMap<>();
        Map<Long, String> roleIdMap = new HashMap<>();
        Set<Long> deptIds = new LinkedHashSet<>();
        List<Long> uniqueDataIds = new ArrayList<>();
        List<Long> preHandleUniqueDataIds = new ArrayList<>();
        List<Executor> executors = new ArrayList<>();
        CollaboratorRelation collaboratorRelation = new CollaboratorRelation();
        AtomicReference<Map<String, FieldParam>> oldFieldParams = new AtomicReference<>();

        // 校验并回调处理
        Long finalTableId = tableId;
        dataValidateService.validateTableData(req.getForm(), fieldParams, (data, fs) -> {
            if (appAuthorityContext != null){
                appAuthorityContext.writeDataFilter(data, FormFieldConstant.getUpdateExcludedFields(),req.getTableId());
            }else{
                data.entrySet().removeIf(current -> FormFieldConstant.getUpdateExcludedFields().contains(current.getKey()));
            }
        }, ! isImport ? null : (fieldParam, it, data, e) -> {
            it.remove();
        }, isImport ? null : (dataId, subformKey, data, fieldParam) -> {
            if (dataId == null) {
                dataId = 0L;
                if (StringUtils.isNotBlank(subformKey)) {
                    dataId = 1L;
                }
            }
            // 收集需要数据唯一的字段
            if (fieldParam.getUnique() && StringUtils.isBlank(subformKey)) {
                String uniquePreHandler = fieldParam.getUniquePreHandler();
                DataPreHandlerType handlerType = null;
                if (StringUtils.isNotBlank(uniquePreHandler)) {
                    handlerType = DataPreHandlerType.parse(uniquePreHandler);
                }
                if (data.containsKey("id")) {
                    if (handlerType == null) {
                        Map<String, Map<Long, List<Object>>> formData = uniques.computeIfAbsent(mainTableName, k -> new HashMap<>());
                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
                        Object value = data.get(fieldParam.getName());
                        if (value != null) {
                            dataList.add(value);
                        }
                        uniqueDataIds.add(Long.valueOf(String.valueOf(data.get("id"))));
                    } else {
                        Map<String, Map<Long, List<Object>>> formData = preHandleUniques.computeIfAbsent(mainTableName, k -> new HashMap<>());
                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
                        Object value = data.get(fieldParam.getName());
                        if (value != null) {
                            dataList.add(handlerType.apply(value));
                        }
                        preHandleUniqueDataIds.add(Long.valueOf(String.valueOf(data.get("id"))));
                    }
                }
            }
            // 成员校验
            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getDataType())) {
                Object value = data.get(fieldParam.getName());
                if (value instanceof Collection) {
                    MemberFieldDatas dataList = MemberFieldTypeUtil.parseMemberFieldDataList(((Collection<?>) value).stream().map(String::valueOf).collect(Collectors.toList()));
                    dataList.getUserList().forEach(item -> userIdMap.put(item.getRealId(), item.getId()));
                    dataList.getRoleList().forEach(item -> roleIdMap.put(item.getRealId(), item.getId()));
                    dataList.getDeptList().forEach(item -> deptIdMap.put(item.getRealId(), item.getId()));
                }
            }
            // 部门校验
            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getDataType())) {
                Object value = data.get(fieldParam.getName());
                if(value instanceof Collection){
                    deptIds.addAll(((Collection<?>) value).stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList()));
                }
            }
        }, (fs, tableDatas) -> {
            if (tableDatas.getData().containsKey(FormFieldConstant.ID)) {
                Long id = Long.valueOf((String.valueOf(tableDatas.getData().get(FormFieldConstant.ID))));
                //协作人
                Map<String, Object> allCollaborators = new HashMap<>();
                //协作人角色
                if (datasMap.get(id) != null) {
                    Map<String, Object> oldData = datasMap.get(id);
                    allCollaborators = dataCollaboratorService.getUpdateCollaborators(tableDatas.getData(),  oldData, bizForm.getFieldParams());
                    dataCollaboratorService.handleDataUpdate(orgId, userId, appId, finalTableId, tableDatas.getData(), oldData, bizForm.getFieldParams(), oldFieldParams, collaboratorRelation);
                    dataRelatingService.handleRelatingDataUpdate(orgId, executors, mainTableName, tableDatas.getData(), userId, oldData, bizForm.getFieldParams());
                }

                List<com.polaris.lesscode.dc.internal.dsl.Set> sets = buildUpdateData(tableDatas.getData(), dtNow, userId, allCollaborators);
                executors.add(Executor.update(mainTable).sets(sets).where(Conditions.and(
                        Conditions.equal("id", id),
                        Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)
                )));
            }
        });

//        log.info("collaboratorRelation :{}, datasMap:{}, tableId:{}", collaboratorRelation, datasMap, tableId);

        // 高级校验
        if (! isImport) {
            dataValidateService.validateUnique(uniques, false, uniqueDataIds);
            dataValidateService.validateUnique(preHandleUniques, true, preHandleUniqueDataIds);
            dataValidateService.validateMembers(orgId, userIdMap, roleIdMap, deptIdMap);
            dataValidateService.validateDeprtments(orgId, deptIds);
        }
        dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors);
        return req.getForm();
    }

//    public List<Map<String, Object>> updateSub(Long orgId, Long userId, Long appId, Long dataId, String fieldKey, List<Map<String, Object>> datas) {
//        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
////        if (! appAuthorityContext.getAppAuthorityResp().hasFieldWriteAuth(fieldKey)){
////            throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE);
////        }
//
//        BizForm bizForm = summaryService.getBizForm(orgId, appId);
//
//        // 获取字段配置
//        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
//        fieldParams.putAll(FormFieldConstant.getCommonFields());
//
//        // 属性
//        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
//        String subTableName = SqlUtil.wrapperSubTableName(bizForm.getTableName(), fieldKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);
//        Table subTable = new Table(subTableName);
//
//        // 子表表头获取
//        FieldParam subFieldParam = fieldParams.get(fieldKey);
//        if (subFieldParam == null){
//            throw new BusinessException(ResultCode.SUB_TABLE_NOT_EXIST);
//        }
//        List<FieldParam> subTableFields = new ArrayList<>();
//        if(CollectionUtils.isNotEmpty(subFieldParam.getFields())){
//            subTableFields.addAll(subFieldParam.getFields());
//        }
//        subTableFields.addAll(FormFieldConstant.getCommonFields().values());
//
//        //校验
//        Map<String, Map<String, Map<Long, List<Object>>>> uniques = new HashMap<>();
//        Map<String, Map<String, Map<Long, List<Object>>>> preHandleUniques = new HashMap<>();
//        Map<Long, String> userIdMap = new HashMap<>();
//        Map<Long, String> deptIdMap = new HashMap<>();
//        Map<Long, String> roleIdMap = new HashMap<>();
//        Set<Long> deptIds = new LinkedHashSet<>();
//        List<Long> uniqueDataIds = new ArrayList<>();
//        List<Long> preHandleUniqueDataIds = new ArrayList<>();
//        LinkedList<Executor> executors = new LinkedList<>();
//
//        Map<String, FieldParam> subTableFieldParams = subTableFields.stream().collect(Collectors.toMap(FieldParam::getName, fieldParam -> fieldParam));
//        dataValidateService.validateTableData(datas, subTableFieldParams, (data, fs) -> {
//            appAuthorityContext.writeDataFilter(data, FormFieldConstant.getUpdateExcludedFields(),0L);
//        }, null, (did, subformKey, data, fieldParam) -> {
//            // 收集需要数据唯一的字段
//            if (fieldParam.getUnique() && StringUtils.isBlank(subformKey)) {
//                String uniquePreHandler = fieldParam.getUniquePreHandler();
//                DataPreHandlerType handlerType = null;
//                if (StringUtils.isNotBlank(uniquePreHandler)) {
//                    handlerType = DataPreHandlerType.parse(uniquePreHandler);
//                }
//                if(data.containsKey("id")){
//                    if (handlerType == null) {
//                        Map<String, Map<Long, List<Object>>> formData = uniques.computeIfAbsent(subTableName, k -> new HashMap<>());
//                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
//                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
//                        Object value = data.get(fieldParam.getName());
//                        if (value != null) {
//                            dataList.add(value);
//                        }
//                        uniqueDataIds.add(Long.valueOf(String.valueOf(data.get("id"))));
//                    } else {
//                        Map<String, Map<Long, List<Object>>> formData = preHandleUniques.computeIfAbsent(subTableName, k -> new HashMap<>());
//                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
//                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
//                        Object value = data.get(fieldParam.getName());
//                        if (value != null) {
//                            dataList.add(handlerType.apply(value));
//                        }
//                        preHandleUniqueDataIds.add(Long.valueOf(String.valueOf(data.get("id"))));
//                    }
//                }
//            }
//            // 成员校验
//            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getDataType())) {
//                Object value = data.get(fieldParam.getName());
//                if(value instanceof Collection){
//                    MemberFieldDatas dataList = MemberFieldTypeUtil.parseMemberFieldDataList(((Collection<?>) value).stream().map(String::valueOf).collect(Collectors.toList()));
//                    dataList.getUserList().forEach(item -> userIdMap.put(item.getRealId(), item.getId()));
//                    dataList.getRoleList().forEach(item -> roleIdMap.put(item.getRealId(), item.getId()));
//                    dataList.getDeptList().forEach(item -> deptIdMap.put(item.getRealId(), item.getId()));
//                }
//            }
//            // 部门校验
//            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getDataType())) {
//                Object value = data.get(fieldParam.getName());
//                if(value instanceof Collection){
//                    deptIds.addAll(((Collection<?>) value).stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList()));
//                }
//            }
//        }, (fs, tableDatas) -> {
//            if(tableDatas.getData().containsKey("id")){
//                Long id = Long.valueOf((String.valueOf(tableDatas.getData().get("id"))));
//                Map<String, Object> data = buildUpdateData(tableDatas.getData(), dtNow, userId, null);
//                executors.add(Executor.update(subTable).set(Sets.setJsonB("data", data)).where(Conditions.and(Conditions.equal("id", id), Conditions.equal("parent_id", dataId))));
//            }
//        });
//
//        // 高级校验
//        dataValidateService.validateUnique(uniques, false, uniqueDataIds);
//        dataValidateService.validateUnique(preHandleUniques, true, preHandleUniqueDataIds);
//        dataValidateService.validateMembers(orgId, userIdMap, roleIdMap, deptIdMap);
//        dataValidateService.validateDeprtments(orgId, deptIds);
//
//        //更新
//        if(CollectionUtils.isNotEmpty(executors)){
//            dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors);
//        }
//        return datas;
//    }

    public Integer recycle(Long orgId, Long userId, Long appId, Long tableId, List<Long> dataIds, List<Long> issueIds, boolean isInternal) {
        BizForm bizForm = summaryService.getBizForm(orgId, appId, tableId);
        String tableName = bizForm.getTableName();
        LinkedList<Executor> executors = new LinkedList<>();
        Map<String, Object> updateData = new HashMap<>();
        String currentTime = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        updateData.put(CommonField.UPDATE_TIME, currentTime);
        updateData.put(CommonField.UPDATOR, userId);
        updateData.put(CommonField.RECYCLE_FLAG, CommonConsts.TRUE);
        updateData.put(CommonField.RECYCLE_TIME, currentTime);

        // 处理关联字段、前后置字段的关联更新，以及协作人相关更新
        dataIds = recycleUpdateSpecialFieldsData(orgId, userId, appId, tableId, bizForm, dataIds, issueIds, updateData, executors);

        return update(orgId, userId, appId, dataIds, updateData, isInternal, tableName, executors);
    }

    public Integer recover(Long orgId, Long userId, Long appId, Long tableId, List<Long> dataIds, List<Long> issueIds, boolean isInternal) {
        BizForm bizForm = summaryService.getBizForm(orgId, appId, tableId);
        String tableName = bizForm.getTableName();
        LinkedList<Executor> executors = new LinkedList<>();
        Map<String, Object> updateData = new HashMap<>();
        String currentTime = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        updateData.put(CommonField.UPDATE_TIME, currentTime);
        updateData.put(CommonField.UPDATOR, userId);
        updateData.put(CommonField.RECYCLE_FLAG, CommonConsts.FALSE);

        // 处理关联字段、前后置字段的关联更新，以及协作人相关更新
        dataIds = recycleUpdateSpecialFieldsData(orgId, userId, appId, tableId, bizForm, dataIds, issueIds, updateData, executors);

        return update(orgId, userId, appId, dataIds, updateData, isInternal, tableName, executors);
    }

    // 更新特殊的数据，并返回dataIds，因为极星都是传issueIds过来，其实dataIds只有之前无码表格使用
    private List<Long> recycleUpdateSpecialFieldsData(Long orgId, Long userId, Long appId, Long tableId, BizForm bizForm, List<Long> dataIds,
                                                      List<Long> issueIds, Map<String, Object> updateData, LinkedList<Executor> executors) {

        List<Map<String, Object>> rows = getNeedSpecialUpdateRows(orgId, tableId, bizForm.getTableName(), dataIds, issueIds, bizForm.getFieldParams());
        List<Long> newDataIds = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                dataRelatingService.handleRelatingDataUpdate(orgId, executors, bizForm.getTableName(), updateData, userId, row, bizForm.getFieldParams());
                newDataIds.add((Long) row.get(FormFieldConstant.ID));
            }
            if (tableId != null && !tableId.equals(0L)) {
                if (Objects.equals(updateData.get(CommonField.RECYCLE_FLAG), CommonConsts.TRUE)) {
                    dataCollaboratorService.handleDataDelete(orgId, userId, appId, tableId, rows, bizForm.getFieldParams(), true);
                } else {
                    dataCollaboratorService.handleDataAdd(orgId, userId, appId, tableId, rows, bizForm.getFieldParams(), true);
                }
            }
        }

        return newDataIds;
    }

    // 获取需要特别更新的数据，用于处理前后置、协作人等
    private List<Map<String, Object>> getNeedSpecialUpdateRows(Long orgId, Long tableId, String tableName, List<Long> dataIds, List<Long> issueIds, Map<String, FieldParam> fieldParams) {
        List<String> columns = new ArrayList<>();
        columns.add(FormFieldConstant.ID);
        columns.add(SqlUtil.wrapperJsonColumn(FormFieldConstant.ISSUE_ID));
        if (tableId != null && !tableId.equals(0L) && fieldParams != null) {
            fieldParams.forEach((fieldName, fieldParam) -> {
                if (dataCollaboratorService.checkIsRecordCollaborator(fieldParam) || FormFieldConstant.NEED_DEAL_RELATING_TYPE.contains(fieldParam.getField().getType())) {
                    columns.add(SqlUtil.wrapperJsonColumn(fieldName)  + " \"" + fieldName + "\"");
                }
            });
        }
        Condition condition = Conditions.in(SqlUtil.wrapperJsonColumn(FormFieldConstant.ISSUE_ID), issueIds);
        if (dataIds != null && dataIds.size() > 0) {
            condition = Conditions.in(FormFieldConstant.ID, dataIds);
        }
        return dataFilterService.filterInternalRawSimple(tableName, columns, Conditions.and(condition, Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)));
    }

    public Integer enable(Long orgId, Long userId, Long appId, List<Long> dataIds, boolean isInternal) {
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        String tableName = bizForm.getTableName();
        LinkedList<Executor> executors = new LinkedList<>();
        Map<String, Object> updateData = new HashMap<>();
        String currentTime = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        updateData.put(CommonField.UPDATE_TIME, currentTime);
        updateData.put(CommonField.UPDATOR, userId);
        updateData.put(CommonField.STATUS, CommonConsts.TRUE);
        return update(orgId, userId, appId, dataIds, updateData, isInternal, tableName, executors);
    }

    public Integer disable(Long orgId, Long userId, Long appId, List<Long> dataIds, boolean isInternal) {
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        String tableName = bizForm.getTableName();
        LinkedList<Executor> executors = new LinkedList<>();
        Map<String, Object> updateData = new HashMap<>();
        String currentTime = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        updateData.put(CommonField.UPDATE_TIME, currentTime);
        updateData.put(CommonField.UPDATOR, userId);
        updateData.put(CommonField.STATUS, CommonConsts.FALSE);
        return update(orgId, userId, appId, dataIds, updateData, isInternal, tableName, executors);
    }

    private Integer update(Long orgId, Long userId, Long appId, List<Long> dataIds, Map<String, Object> updateData, boolean isInternal, String tableName, LinkedList<Executor> executors){
        if (CollectionUtils.isEmpty(dataIds)) {
            throw new BusinessException(ResultCode.APP_VALUE_RECYCLE_FAIL);
        }

        if (! isInternal) {
            AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())) {
                throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE);
            }
        }

        List<com.polaris.lesscode.dc.internal.dsl.Set> sets = buildUpdateSets(updateData);
        executors.addFirst(Executor.update(new Table(tableName))
                .sets(sets)
                .where(Conditions.and(
                        Conditions.in("id", dataIds.toArray()),
                        Conditions.equal(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORG_ID), orgId)
                )));
        return dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors).getData()[0];
    }

    /**
     * 构建插入数据
     *
     * @Author Nico
     * @Date 2021/2/25 14:25
     **/
    public static List<com.polaris.lesscode.dc.internal.dsl.Set> buildUpdateData(Map<String, Object> data, String updateTime, Long operatorId, Map<String, Object> collaborators){
        List<com.polaris.lesscode.dc.internal.dsl.Set> sets = new ArrayList<>();
        if (data == null) {
            return sets;
        }
        data.entrySet().removeIf(current -> FormFieldConstant.getUpdateExcludedFields().contains(current.getKey()));

        // set
        data.put(FormFieldConstant.UPDATOR, operatorId);
        data.put(FormFieldConstant.UPDATE_TIME, updateTime);
        data.put(FormFieldConstant.COLLABORATORS, DataAddService.buildCollaborators(collaborators));

        return buildUpdateSets(data);
    }

    public static List<com.polaris.lesscode.dc.internal.dsl.Set> buildUpdateSets(Map<String, Object> data) {
        List<com.polaris.lesscode.dc.internal.dsl.Set> sets = new ArrayList<>();
        Map<String, Object> jsonData = new HashMap<>();
        data.forEach((key, value) -> {
            if (SqlUtil.notJsonField.contains(key)) {
                sets.add(Sets.set(key, value));
            } else {
                jsonData.put(key, value);
            }
        });
        sets.add(Sets.setJsonB(FormFieldConstant.DATA, jsonData));

        return sets;
    }

    /**
     * 移动数据
     *
     * @Author Nico
     * @Date 2021/3/12 18:12
     **/
    public Map<Long, Double> moving(Long orgId, Long userId, Long appId, AppValueMovingReq req, boolean isInternal){
        if (!isInternal){
            AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())){
                throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE);
            }
        }
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        String tableName = bizForm.getTableName();
        return moving(tableName, Collections.singletonList(req.getDataId()), req.getBeforeId(), req.getAfterId(), req.isAsc());
    }

    public Map<Long, Double> moving(String tableName, Collection<Long> dataIds, Long beforeId, Long afterId, boolean asc) {
        Map<Long, Double> result = new HashMap<>();

        if (CollectionUtils.isEmpty(dataIds)){
            return result;
        }
        if (Objects.equals(beforeId, 0L)){
            beforeId = null;
        }
        if (Objects.equals(afterId, 0L)){
            afterId = null;
        }

        if (beforeId == null && afterId == null){
            Query beforeQuery = Query.select()
                    .from(new Table(tableName))
                    .where(Conditions.and(
                            Conditions.notNil(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORDER))
                    )).limit(1)
                    .order(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORDER), ! asc, true);
            log.info("moving {}", beforeQuery.toSql());
            List<Map<String, Object>> beforeData = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), beforeQuery).getData();
            if (CollectionUtils.isNotEmpty(beforeData)){
                Object beforeObj = beforeData.get(0).get(FormFieldConstant.ID);
                if (beforeObj instanceof Number){
                    beforeId = ((Number) beforeObj).longValue();
                }
            }
        }

        List<Long> referenceDataIds = new ArrayList<>();
        if (beforeId != null) referenceDataIds.add(beforeId);
        if (afterId != null) referenceDataIds.add(afterId);

        Query query = null;
        if (CollectionUtils.isNotEmpty(referenceDataIds)){
            query = Query.select().from(new Table(tableName)).where(Conditions.in(FormFieldConstant.ID, referenceDataIds.toArray()));
        }

        List<Executor> executors = new ArrayList<>();
        Number targetOrder = null;
        if (query != null){
            List<Map<String, Object>> list = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
            if (CollectionUtils.isEmpty(list)){
                throw new BusinessException(ResultCode.ORDER_REFER_DATA_IS_NULL);
            }

            Map<Long, Number> referDatas = new HashMap<>();
            for (Map<String, Object> referData: list){
                Object order = referData.get(FormFieldConstant.ORDER);
                if (order instanceof Number){
                    referDatas.put(((Number)referData.get(FormFieldConstant.ID)).longValue(), (Number) order);
                }
            }

            if (MapUtils.isNotEmpty(referDatas)){
                Number beforeOrder = referDatas.get(beforeId);
                Number afterOrder = referDatas.get(afterId);
                boolean isMiddle = false;
                if (beforeOrder != null && afterOrder != null){
                    targetOrder = (beforeOrder.doubleValue() + afterOrder.doubleValue()) / 2;
                    isMiddle = true;
                }else if (beforeOrder != null){
                    targetOrder = beforeOrder.doubleValue() + (asc ? 65536 : - 65536);
                }else if (afterOrder != null){
                    targetOrder = afterOrder.doubleValue() + (asc ? - 65536 : 65536);
                }

                if (targetOrder != null){
                    if (isMiddle && Math.abs(targetOrder.doubleValue() - beforeOrder.doubleValue()) < 1) {
                        if (asc){
                            targetOrder = beforeOrder.doubleValue() + 65535;
                            executors.add(Executor.update(new Table(tableName))
                                    .set(Sets.set(FormFieldConstant.ORDER, FormFieldConstant.ORDER + " - 131072)"))
                                    .where(Conditions.and(
                                            Conditions.gte(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORDER), beforeOrder.doubleValue()),
                                            Conditions.gt(FormFieldConstant.ID, beforeId)
                                    )));
                        } else {
                            targetOrder = beforeOrder.doubleValue() - 65535;
                            executors.add(Executor.update(new Table(tableName))
                                    .set(Sets.set(FormFieldConstant.ORDER, FormFieldConstant.ORDER + " - 131072)"))
                                    .where(Conditions.and(
                                            Conditions.lte(SqlUtil.wrapperJsonColumn(FormFieldConstant.ORDER), beforeOrder.doubleValue()),
                                            Conditions.gt(FormFieldConstant.ID, beforeId)
                                    )));
                        }
                    }
                }
            }
        } else {
            targetOrder = 65535L;
        }
        if (targetOrder == null) {
            targetOrder = 65535L;
        }

        for (int i = 0; i < dataIds.size(); i++) {
            Long dataId = (Long)dataIds.toArray()[i];
            if (i > 0) {
                targetOrder = targetOrder.doubleValue() + (asc ? 65536 : - 65536);
            }

            result.put(dataId, targetOrder.doubleValue());
            executors.add(Executor.update(new Table(tableName))
                    .set(Sets.setJsonB("data." + FormFieldConstant.ORDER, targetOrder))
                    .where(Conditions.equal(FormFieldConstant.ID, dataId)));
        }
        dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors);
        return result;
    }
}
