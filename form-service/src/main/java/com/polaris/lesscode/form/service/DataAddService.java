package com.polaris.lesscode.form.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.*;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.enums.DataPreHandlerType;
import com.polaris.lesscode.form.handler.GetFieldValue;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.*;
import com.polaris.lesscode.form.req.*;
import com.polaris.lesscode.form.util.*;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
public class DataAddService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private DataValidateService dataValidateService;

    @Autowired
    private DataUpdateService dataUpdateService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private DataFilterService dataFilterService;

    @Autowired
    private DataRelatingService dataRelatingService;

    @Autowired
    private DataCollaboratorService dataCollaboratorService;

    @Autowired
    private AppApi appApi;

    public Collection<Map<String, Object>> add(Long orgId, Long userId, Long appId, AppValueAddReq req) {
        return add(orgId, userId, appId, req, false, false, false);
    }

    public Collection<Map<String, Object>> add(Long orgId, Long userId, Long appId, AppValueAddReq req,
                                               boolean isInternal, boolean isImport, boolean isCreateTemplate) {
        List<Map<String, Object>> datas = req.getForm();

        AppAuthorityContext appAuthorityContext = isInternal ? null : permissionService.appAuth(orgId, appId, userId);
        if (appAuthorityContext != null) {
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_CREATE.getCode())) {
                throw new BusinessException(ResultCode.FORM_OP_NO_CREATE);
            }
        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId, req.getTableId());

        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
//        fieldParams.putAll(FormFieldConstant.getCommonFields());

        // 初始化必要参数
        String mainTableName = bizForm.getTableName();
        Table mainTable = new Table(mainTableName);
        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());

        //start
        Map<String, Map<String, Map<Long, List<Object>>>> uniques = new HashMap<>();
        Map<String, Map<String, Map<Long, List<Object>>>> preHandleUniques = new HashMap<>();
        Map<Long, String> userIdMap = new HashMap<>();
        Map<Long, String> deptIdMap = new HashMap<>();
        Map<Long, String> roleIdMap = new HashMap<>();
        Set<Long> deptIds = new LinkedHashSet<>();
        LinkedList<Executor> executors = new LinkedList<>();
        List<com.polaris.lesscode.dc.internal.dsl.Value> mainValues = new ArrayList<>();
        Map<Long, Map<String, Object>> mainDatas = new LinkedHashMap<>();
        Map<Long, Map<String, AtomicLong>> subDataAutoIds = new HashMap<>();
        List<ModifyData> modifyDatas = new ArrayList<>();
        //协作人
//        CollaboratorRelation collaboratorRelation = new CollaboratorRelation();

        if (!CollectionUtils.isEmpty(datas)) {
            // 处理递增字，没有子表，之后估计要加子表的递增
            handleAutoIncrement(mainTableName, fieldParams, null, datas);
            // 校验并回调处理
            dataValidateService.validateTableData(datas, fieldParams, (data, fs) -> {
                if (!isInternal){
                    if (appAuthorityContext != null){
                        appAuthorityContext.writeDataFilter(data, FormFieldConstant.getInsertExcludedFields(),req.getTableId());
                    }else{
                        data.entrySet().removeIf(current -> FormFieldConstant.getInsertExcludedFields().contains(current.getKey()));
                    }
                }
                dataValidateService.assemblyDefaultValue(data, fs, userId);
            }, !isImport ? null : (fieldParam, it, data, err) -> {
                it.remove();
            }, isImport ? null : (dataId, subformKey, data, fieldParam) -> {
                if (dataId == null){
                    dataId = 0L;
                    if (StringUtils.isNotBlank(subformKey)){
                        dataId = 1L;
                    }
                }
                // 收集需要唯一的字段
                if (fieldParam.getUnique()) {
                    String uniquePreHandler = fieldParam.getUniquePreHandler();
                    DataPreHandlerType handlerType = null;
                    if (StringUtils.isNotBlank(uniquePreHandler)) {
                        handlerType = DataPreHandlerType.parse(uniquePreHandler);
                    }
                    if (handlerType == null) {
                        Map<String, Map<Long, List<Object>>> formData = uniques.computeIfAbsent(mainTableName, k -> new HashMap<>());
                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
                        Object value = data.get(fieldParam.getName());
                        if (value != null) {
                            dataList.add(value);
                        }
                    } else {
                        Map<String, Map<Long, List<Object>>> formData = preHandleUniques.computeIfAbsent(mainTableName, k -> new HashMap<>());
                        Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
                        List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
                        Object value = data.get(fieldParam.getName());
                        if (value != null) {
                            dataList.add(handlerType.apply(value));
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
                    if(value instanceof Collection) {
                        deptIds.addAll(((Collection<?>) value).stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList()));
                    }
                }
            }, (currentFieldParams, tableDatas) -> {
                //协作人
                Map<String, Object> allCollaborators = dataCollaboratorService.getCreateCollaborators(tableDatas.getData(), currentFieldParams);

                InsertData insertData = buildInsertData(bizForm.getAppId(), tableDatas.getData(), dtNow, userId, allCollaborators, isInternal);

                mainValues.add(Values.value(insertData.getListData()));
                mainDatas.put(insertData.getMainId(), insertData.getMainData());

                // 创建模板时，不处理关联前后置的关联更新
                if (!isCreateTemplate) {
                    // 处理关联字段、前后置字段的关联更新
                    dataRelatingService.handleRelatingDataAdd(orgId, executors, mainTableName, tableDatas, Long.valueOf(tableDatas.getData().get(FormFieldConstant.ISSUE_ID).toString()), userId, bizForm.getFieldParams());
                }

//                if (req.getTableId() != null && !req.getTableId().equals(0L)) {
//                    List<CollaboratorColumnUser> users = dataCollaboratorService.handleDataAdd(orgId, userId, appId, req.getTableId(), tableDatas.getData(), bizForm.getFieldParams());
//                    collaboratorRelation.addUser(users);
//                }
            });
        }

        if (CollectionUtils.isNotEmpty(modifyDatas)) {
            for (ModifyData modifyData: modifyDatas) {
                modifyData.apply();
            }
        }

        // 高级校验
        if (! isImport) {
            dataValidateService.validateUnique(uniques, false, null);
            dataValidateService.validateUnique(preHandleUniques, true, null);
            dataValidateService.validateMembers(orgId, userIdMap, roleIdMap, deptIdMap);
            dataValidateService.validateDeprtments(orgId, deptIds);
        }

        if (!CollectionUtils.isEmpty(mainValues)) {
            List<String> columns = new ArrayList<>(SqlUtil.notJsonField);
            columns.add(FormFieldConstant.DATA);
            executors.addFirst(Executor.insert(mainTable).columns(columns).values(mainValues));
            dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executors).getData();
        }
        if (!MapUtils.isEmpty(mainDatas)) {
            List<Long> sortIds = new ArrayList<>();
            for (Map.Entry<Long, Map<String, Object>> entry: mainDatas.entrySet()) {
                if (! entry.getValue().containsKey("order")) {
                    sortIds.add(entry.getKey());
                }
            }
            Map<Long, Double> orders = dataUpdateService.moving(mainTableName, sortIds, req.getBeforeId(), req.getAfterId(), req.isAsc());
            orders.entrySet().forEach(e -> { // 没有传order过来的才由form设置order
                mainDatas.get(e.getKey()).put(FormFieldConstant.ORDER, e.getValue());
            });
        }

        // 把ID设置到返回值里
        mainDatas.entrySet().forEach(e -> {
            e.getValue().put(FormFieldConstant.ID, String.valueOf(e.getKey()));
        });
        return mainDatas.values();
    }

//    public Collection<Map<String, Object>> addSub(Long orgId, Long userId, Long appId, Long dataId, String fieldKey, List<Map<String, Object>> datas) {
//        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
////        if (! appAuthorityContext.getAppAuthorityResp().hasFieldWriteAuth(fieldKey)){
////            throw new BusinessException(ResultCode.FORM_OP_NO_CREATE);
////        }
//
//        BizForm bizForm = summaryService.getBizForm(orgId, appId);
//        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
//        fieldParams.putAll(FormFieldConstant.getCommonFields());
//
//        FieldParam subFieldParam = fieldParams.get(fieldKey);
//        if (subFieldParam == null){
//            throw new BusinessException(ResultCode.SUB_TABLE_NOT_EXIST);
//        }
//        List<FieldParam> subTableFields = subFieldParam.getFields();
//        subTableFields.addAll(FormFieldConstant.getCommonFields().values());
//
//        // 初始化必要参数
//        String subTableName = SqlUtil.wrapperSubTableName(bizForm.getTableName(), fieldKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);
//        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
//
//        Map<String, Map<String, Map<Long, List<Object>>>> uniques = new HashMap<>();
//        Map<String, Map<String, Map<Long, List<Object>>>> preHandleUniques = new HashMap<>();
//        Map<Long, String> userIdMap = new HashMap<>();
//        Map<Long, String> deptIdMap = new HashMap<>();
//        Map<Long, String> roleIdMap = new HashMap<>();
//        Set<Long> deptIds = new LinkedHashSet<>();
//        LinkedList<Executor> executors = new LinkedList<>();
//        List<com.polaris.lesscode.dc.internal.dsl.Value> subValues = new ArrayList<>();
//        Map<Long, Map<String, Object>> subDatas = new LinkedHashMap<>();
//
//        Map<String, FieldParam> subTableFieldParams = subTableFields.stream().collect(Collectors.toMap(FieldParam::getName, fieldParam -> fieldParam));
//
//        // 处理递增字，没有子表，之后估计要加子表的递增
//        handleAutoIncrement(subTableName, subTableFieldParams, dataId, datas);
//
//        dataValidateService.validateTableData(datas, subTableFieldParams, (data, fs) -> {
//            appAuthorityContext.writeDataFilter(data, FormFieldConstant.getInsertExcludedFields(),0L);
//            dataValidateService.assemblyDefaultValue(data, fs, userId);
//        }, null, (did, subformKey, data, fieldParam) -> {
//            // 收集需要唯一的字段
//            if (fieldParam.getUnique()) {
//                String uniquePreHandler = fieldParam.getUniquePreHandler();
//                DataPreHandlerType handlerType = null;
//                if (StringUtils.isNotBlank(uniquePreHandler)) {
//                    handlerType = DataPreHandlerType.parse(uniquePreHandler);
//                }
//                if (handlerType == null) {
//                    Map<String, Map<Long, List<Object>>> formData = uniques.computeIfAbsent(subTableName, k -> new HashMap<>());
//                    Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
//                    List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
//                    Object value = data.get(fieldParam.getName());
//                    if (value != null) {
//                        dataList.add(value);
//                    }
//                } else {
//                    Map<String, Map<Long, List<Object>>> formData = preHandleUniques.computeIfAbsent(subTableName, k -> new HashMap<>());
//                    Map<Long, List<Object>> dataMap = formData.computeIfAbsent(fieldParam.getName(), k -> new HashMap<>());
//                    List<Object> dataList = dataMap.computeIfAbsent(dataId, k -> new ArrayList<>());
//                    Object value = data.get(fieldParam.getName());
//                    if (value != null) {
//                        dataList.add(handlerType.apply(value));
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
//
//            }
//            // 部门校验
//            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getDataType())) {
//                Object value = data.get(fieldParam.getName());
//                if(value instanceof Collection){
//                    deptIds.addAll(((Collection<?>) value).stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList()));
//                }
//            }
//        }, (fs, tableDatas) -> {
//            Map<String, Object> subData = buildInsertData(bizForm.getAppId(), tableDatas.getData(), dtNow, userId, null, false);
//            Long subId = IdWorker.getId();
//            subData.put(FormFieldConstant.ORDER, subId);
//            subValues.add(Values.value(subId, dataId, subData));
//            subDatas.put(subId, subData);
//        });
//
//        // 独立校验
//        dataValidateService.validateUnique(uniques, false, null);
//        dataValidateService.validateUnique(preHandleUniques, true, null);
//        dataValidateService.validateMembers(orgId, userIdMap, roleIdMap, deptIdMap);
//        dataValidateService.validateDeprtments(orgId, deptIds);
//
//        if (!CollectionUtils.isEmpty(subValues)) {
//            Integer affects = dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), Executor.insert(new Table(subTableName)).columns("id", "parent_id", "data").values(subValues)).getData();
//            log.info("插入子表单数据响应结果集: {}", affects);
//        }
//        if (!MapUtils.isEmpty(subDatas)){
//            for (Map.Entry<Long, Map<String, Object>> entry: subDatas.entrySet()){
//                entry.getValue().put(FormFieldConstant.ID, entry.getKey());
//            }
//        }
//        return subDatas.values();
//    }

    private Long autoIncrementInitialNum(FieldParam fieldParam){
        // 递增赋值
        Long initialNum = null;
        if("autonum".equals(fieldParam.getField().getType())){    //TODO 不确定哪种方式，都兼容，回头确认了再删掉
            initialNum = 1L;
            Map<String, Object> props = fieldParam.getField().getProps();
            if (MapUtils.isNotEmpty(props)){
                Object obj = props.get("autonum");
                if(obj instanceof Map){
                    Object initialNumObj = ((Map<?, ?>) obj).get("initialNum");
                    if(StringUtils.isNumeric(String.valueOf(initialNumObj))){
                        initialNum = Long.valueOf(String.valueOf(initialNumObj));
                    }
                }
            }
        }
        return initialNum;
    }

    /**
     * 处理自增
     **/
    private void handleAutoIncrement(String tableName, Map<String, FieldParam> fieldParams, Long parentDataId, List<Map<String, Object>> datas){
        Map<String, Long> fieldInitNum = new HashMap<>();
        fieldParams.values().forEach(fieldParam -> {
            // 递增赋值
            Long initialNum = autoIncrementInitialNum(fieldParam);
            if (initialNum != null){
                fieldInitNum.put(fieldParam.getName(), initialNum);
            }
        });

        // 填充自增值
        for(Map.Entry<String, Long> entry: fieldInitNum.entrySet()){
            List<Long> seqs = getBatchIncr(tableName, entry.getKey(), entry.getValue(), datas.size(), parentDataId, this::getMaxValue);
            if (CollectionUtils.isNotEmpty(seqs)){
                for(int index = 0; index < seqs.size(); index ++){
                    datas.get(index).put(entry.getKey(), seqs.get(index));
                }
            }
        }
    }

    /**
     * 构建插入数据
     *
     * @Author Nico
     * @Date 2021/2/25 14:25
     **/
    public InsertData buildInsertData(Long appId, Map<String, Object> data, String insertTime, Long operatorId, Map<String, Object> collaborators, boolean isInternal){
        if(data == null){
            return null;
        }
        // set
        if (!isInternal){
            data.entrySet().removeIf(current -> FormFieldConstant.getInsertExcludedFields().contains(current.getKey()));
        }
        data.putIfAbsent(FormFieldConstant.CREATOR, operatorId);
        data.putIfAbsent(FormFieldConstant.UPDATOR,operatorId);
        data.putIfAbsent(FormFieldConstant.CREATE_TIME, new Timestamp(new Date().getTime()));
        data.putIfAbsent(FormFieldConstant.UPDATE_TIME, new Timestamp(new Date().getTime()));
        data.put(FormFieldConstant.RECYCLE_FLAG, CommonConsts.FALSE);
        data.put(FormFieldConstant.APP_ID, String.valueOf(appId));
        data.put(FormFieldConstant.COLLABORATORS, buildCollaborators(collaborators));

        return buildInsertData(data);
    }

    public InsertData buildInsertData(Map<String, Object> data) {
        Long mainId = IdWorker.getId();
        Map<String,Object> jsonData = new HashMap<>();
        List<Object> listData = new ArrayList<>();
        data.put(FormFieldConstant.ID, mainId);
        SqlUtil.notJsonField.forEach(item -> {
            listData.add(data.get(item));
        });
        data.forEach((key,value)->{
            if (!SqlUtil.notJsonField.contains(key)) {
                jsonData.put(key, value);
            }
        });
        listData.add(jsonData);

        return new InsertData(data,listData, mainId);
    }

    public static String buildCollaborators(Map<String, Object> collaborators) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        if (MapUtils.isNotEmpty(collaborators)) {
            collaborators.forEach((key, value) -> {
                if(value instanceof Collection){
                    ((Collection<?>)value).forEach(item -> {
                        buffer.append(item);
                        buffer.append(",");
                        buffer.append(key);
                        buffer.append(",");
                    });
                }
            });
            if (buffer.length() > 1) {
                buffer.setCharAt(buffer.length() - 1, '}');
            } else {
                buffer.append("}");
            }
        } else {
            buffer.append("}");
        }


        return buffer.toString();
    }

    private Long getMaxValue(String tableName, String fieldKey, Long dataId, Long initialNum, DataCenterProvider dataCenterProvider) {
        Long increment = null;
        Query selectMaxIncrement = Query
                .select("max((" + SqlUtil.wrapperJsonColumn(fieldKey) + ")::text::bigint) as increment")
                .from(new Table(tableName));
        if (dataId != null) {
            selectMaxIncrement.where(Conditions.equal("parent_id", dataId));
        }
        List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), selectMaxIncrement).getData();
        if (!CollectionUtils.isEmpty(datas)) {
            Map<String, Object> incrementMap = datas.get(0);
            Object objIncrement = incrementMap.get("increment");
            if (objIncrement != null) {
                increment = Long.valueOf(objIncrement.toString());
            }
        }

        // pg库没有 查自增字段初始值
        if (increment == null) {
            increment = initialNum - 1;
        }
        return increment;
    }

    /**
     * 批量获取自增数据
     *
     * @param tableName  表名
     * @param fieldKey   自增字段id
     * @param initialNum 自增字段初始值
     * @param count      返回自增数据量
     * @param dataId     主表行数据id
     * @return
     */
    public List<Long> getBatchIncr(String tableName, String fieldKey, Long initialNum, Integer count, Long dataId, GetFieldValue getFieldValue) {
        List<Long> incrList = new ArrayList<>();
        if (count != null && count > 0) {
            String incrKey = tableName + ":" + fieldKey;
            if (dataId != null){
                incrKey += ":" + dataId;
            }
            Long increment = null;
            Object objIncr = redisUtil.get(incrKey);
            if (objIncr != null) {
                increment = redisUtil.incr(incrKey, count);
                long oldIncr = increment - count;
                redisUtil.expire(incrKey, 24, TimeUnit.HOURS);
                for (int i = 0; i < count; i++) {
                    incrList.add(++oldIncr);
                }
            } else {
                String lockKey = tableName + ":" + fieldKey + "lock";
                Boolean getLock = redisUtil.tryLock(lockKey, lockKey, 6 * 1000);
                try {
                    if (Objects.equals(getLock, Boolean.TRUE)) {
                        objIncr = redisUtil.get(incrKey);
                        if (objIncr == null) {
                            increment = getFieldValue.getIncr(tableName, fieldKey, dataId, initialNum, dataCenterProvider);
                        }else{
                            increment = Long.valueOf(objIncr.toString());
                        }
                        redisUtil.set(incrKey, (long) increment + count, 24, TimeUnit.HOURS);
                    }else{
                        throw new BusinessException(ResultCode.OTHER_USER_IN_OPERATION);
                    }
                } catch (Exception e) {
                    increment = 0L;
                    e.printStackTrace();
                } finally {
                    if (getLock){
                        redisUtil.unlock(lockKey, lockKey);
                    }
                }
                if (increment == null) {
                    return incrList;
                }
                for (int i = 0; i < count; i++) {
                    incrList.add(++increment);
                }
            }

        }
        return incrList;
    }
}
