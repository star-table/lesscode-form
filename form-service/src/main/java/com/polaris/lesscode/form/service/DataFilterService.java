package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.*;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.enums.SensitiveType;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.form.FormRefDsl;
import com.polaris.lesscode.form.internal.req.QuerySqlReq;
import com.polaris.lesscode.form.internal.resp.QuerySqlResp;
import com.polaris.lesscode.form.internal.sula.*;
import com.polaris.lesscode.form.internal.util.HeaderUtil;
import com.polaris.lesscode.form.req.AppValueListReq;
import com.polaris.lesscode.form.util.DatePlaceHolderUtils;
import com.polaris.lesscode.form.util.DslUtil;
import com.polaris.lesscode.form.util.MemberFieldTypeUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.gotable.internal.req.ReadSummeryTableIdRequest;
import com.polaris.lesscode.gotable.internal.resp.ReadSummeryTableIdResp;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import com.polaris.lesscode.uc.internal.resp.DeptInfoResp;
import com.polaris.lesscode.uc.internal.resp.RoleInfoResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.GsonUtils;
import com.polaris.lesscode.util.JsonUtils;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.workflow.internal.resp.TodoResp;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据过滤服务
 *
 * @author Nico
 * @date 2021-02-21
 */
@Slf4j
@Service
public class DataFilterService {

    @Autowired
    private AppProvider appProvider;

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private AppApi appApi;

    @Autowired
    private GoTableService goTableService;

    /**
     * 分配最大SIZE
     */
    private static final int MAX_SIZE = 2000;

    public Page<Map<String, Object>> filterInternal(Long orgId, Long operatorId, Long appId, AppValueListReq req) {
//        log.info("request body {}", JsonUtils.toJson(req));
        BizForm bizForm = summaryService.getBizForm(orgId, appId, req.getTableId());
        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
        if (req.isNeedRefColumn()) {
            bizForm.setRelateTablesFieldParams(getAllRelateTableFields(fieldParams,req.getFilterColumns(),orgId,operatorId));
        }
//        log.info("fieldParams {}", JsonUtils.toJson(fieldParams));
        // 查询数据
        Page<Map<String, Object>> pages = listFilter(orgId, operatorId, bizForm, req);
        if(CollectionUtils.isNotEmpty(pages.getList())){
//            List<Long> dataIds = pages.getList().stream().map(data -> Long.valueOf(String.valueOf(data.get("id")))).collect(Collectors.toList());

            // 收集数据的特殊字段信息，例如哪些是引用字段，哪些是关联字段，哪些是成员字段等等
            DataFilterFieldCollector fieldCollector = fieldCollect(bizForm, req.getColumns());

            long start = System.currentTimeMillis();
            // 通过收集到的特殊字段信息，从数据中获取必要的信息方便之后的特殊字段对应的数据查询，例如引用表的数据，子表数据等等
            DataFilterResourceCollector resourceCollector = resourceCollect(fieldCollector, pages.getList());
            long end = System.currentTimeMillis();
            log.info("resourceCollect spend {}", end - start);

            start = System.currentTimeMillis();
            // 收集特殊字段对应的数据
            DataFilterDataCollector dataCollector = dataCollect(bizForm, fieldCollector, resourceCollector);
            end = System.currentTimeMillis();
            log.info("dataCollect spend {}", end - start);

            start = System.currentTimeMillis();
            // 将特殊字段对应的数据装配入原始数据
            dataAssembly(fieldCollector, dataCollector, pages.getList());
            end = System.currentTimeMillis();
            log.info("dataAssembly spend {}", end - start);
        }
        return pages;
    }

    public List<Map<String, Object>> filterInternalRaw(Long orgId, Long operatorId, AppValueListReq req) {
        ReadSummeryTableIdResp summeryTableIdResp = goTableService.readSummeryTableId(new ReadSummeryTableIdRequest(orgId), orgId, operatorId);
        String tableName = SqlUtil.wrapperTableName(orgId, summeryTableIdResp.getTableId());

        return filterInternalRaw(tableName, req.getFilterColumns(), req.getCondition(), req.getPage(), req.getSize(), req.getOrders());
    }

    public List<Map<String, Object>> filterInternalRawSimple(String tableName, List<String> columns, Condition condition) {
        return filterInternalRaw(tableName, columns, condition, 0, 0, null);
    }

    public List<Map<String, Object>> filterInternalRaw(String tableName, List<String> columns, Condition condition, Integer page, Integer size, List<Order> orders) {
        // conditions
        List<Condition> conditions = new ArrayList<>();
        if (condition != null && StringUtils.isNotBlank(condition.getType())){
            conditions.add(condition);
        }

        // 拼装请求
        Query query;
        if (CollectionUtils.isEmpty(columns)) {
            query = Query.select();
        } else {
            query = Query.select(columns);
        }
        query = query.from(new Table(tableName)).where(Conditions.and(conditions));
        if (page > 0 && size > 0) {
            query.limit((page-1)*size, size);
        }
        if (orders != null && orders.size() > 0) {
            query.orders(orders);
        }

        return dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
    }

    public List<Map<String, Object>> filterInternalCustomStat(Long orgId, Long operatorId, Long appId, AppValueListReq req) {
        log.info("request body {}", JsonUtils.toJson(req));
        BizForm bizForm = summaryService.getBizForm(orgId, appId, req.getTableId());
        // 如果存在继承，则拼接继承条件
        if (bizForm.hasExtends()){
            Condition extendsCond = Conditions.or(Conditions.equal(FormFieldConstant.APP_ID, appId.toString()));
            if(req.getCondition() == null){
                req.setCondition(extendsCond);
            }else{
                req.setCondition(Conditions.and(req.getCondition(), extendsCond));
            }
        }
        return listFilterCustomStat(orgId, operatorId, bizForm, req);
    }

    public Long filterInternalStat(Long orgId, Long operatorId, Long appId, AppValueListReq req) {
        BizForm bizForm = summaryService.getBizForm(orgId, appId, req.getTableId());
        // 如果存在继承，则拼接继承条件
        if (bizForm.hasExtends()){
            Condition extendsCond = Conditions.or(Conditions.equal(FormFieldConstant.APP_ID, appId.toString()));
            if(req.getCondition() == null){
                req.setCondition(extendsCond);
            }else{
                req.setCondition(Conditions.and(req.getCondition(), extendsCond));
            }
        }

        // 查询数据
        Page<Map<String, Object>> pages = listFilter(orgId, operatorId, bizForm, req);

        return pages.getTotal();
    }

    public Page<Map<String, Object>> filter(Long orgId, Long operatorId, Long appId, AppValueListReq req) {
        // 权限判断
        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, operatorId);
        if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_READ.getCode())){
            throw new BusinessException(ResultCode.FORM_OP_NO_READ);
        }

        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        // 如果存在继承，则拼接继承条件
        if (bizForm.hasExtends()){
            Condition extendsCond = Conditions.or(Conditions.equal(FormFieldConstant.APP_ID, appId.toString()));
            if(req.getCondition() == null){
                req.setCondition(extendsCond);
            }else{
                req.setCondition(Conditions.and(req.getCondition(), extendsCond));
            }
        }
        // 数据域权限
        if (! appAuthorityContext.getAppAuthorityResp().hasAppRootPermission()){
            AppAuthorityResp appAuthorityResp = appAuthorityContext.getAppAuthorityResp();
            if (CollectionUtils.isNotEmpty(appAuthorityResp.getDataAuth())){
                appAuthorityResp.getDataAuth().removeIf(Objects::isNull);
                if (CollectionUtils.isNotEmpty(appAuthorityResp.getDataAuth())){
                    if (req.getCondition() == null){
                        req.setCondition(Conditions.and(appAuthorityResp.getDataAuth()));
                    }else{
                        req.setCondition(Conditions.and(Conditions.or(appAuthorityResp.getDataAuth()), req.getCondition()));
                    }
                }
            }
        }

        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
        // 查询数据
        Page<Map<String, Object>> pages = listFilter(orgId, operatorId, bizForm, req);
        if(CollectionUtils.isNotEmpty(pages.getList())){
//            List<Long> dataIds = pages.getList().stream().map(data -> Long.valueOf(String.valueOf(data.get("id")))).collect(Collectors.toList());

            // 去除不可读字段
            appAuthorityContext.readDataFilter(pages.getList(), FormFieldConstant.getSelectExcludedFields(), req.getTableId());

            // 收集数据的特殊字段信息，例如哪些是引用字段，哪些是关联字段，哪些是成员字段等等
            DataFilterFieldCollector fieldCollector = fieldCollect(bizForm, req.getColumns());

            // 通过收集到的特殊字段信息，从数据中获取必要的信息方便之后的特殊字段对应的数据查询，例如引用表的数据，子表数据等等
            DataFilterResourceCollector resourceCollector = resourceCollect(fieldCollector, pages.getList());

            // 收集特殊字段对应的数据
            DataFilterDataCollector dataCollector = dataCollect(bizForm, fieldCollector, resourceCollector);

            // 将特殊字段对应的数据装配入原始数据
            dataAssembly(fieldCollector, dataCollector, pages.getList());

            // 再次去除不可读字段，原因：关联表查询的记录会在上一步中插入，逻辑尚可优化
            appAuthorityContext.readDataFilter(pages.getList(), FormFieldConstant.getSelectExcludedFields(),req.getTableId());

            // 脱敏
            desensitization(bizForm, pages.getList(), appAuthorityContext,req.getTableId());

            // 处理子数据数量
            // handleParentChildData(bizForm, pages.getList());
        }
        return pages;
    }

    private void desensitization(BizForm bizForm, List<Map<String, Object>> datas, AppAuthorityContext appAuthorityContext, Long tableId){
        for(Map.Entry<String, FieldParam> entry: bizForm.getFieldParams().entrySet()){
            String fieldName = entry.getKey();
            FieldParam fieldParam = entry.getValue();
            if (Objects.equals(fieldParam.getSensitiveFlag(), CommonConsts.TRUE) && appAuthorityContext.getAppAuthorityResp().hasFieldMaskingAuth(tableId, fieldName)){
                for (Map<String, Object> data: datas){
                    Object desensitizationData = SensitiveType.parse(fieldParam.getSensitiveStrategy()).getFilter().desensitization(data.get(fieldName));
                    data.put(fieldName, desensitizationData);
                }
            }
        }
    }

    private void handleParentChildData(BizForm bizForm, List<Map<String, Object>> datas){
        if (CollectionUtils.isEmpty(datas)){
            return;
        }
        List<String> ids = new ArrayList<>();
        for (Map<String, Object> data: datas){
            String idStr = String.valueOf(data.get("id"));
            if (StringUtils.isNumeric(idStr)){
                ids.add(idStr);
            }
        }
        // 统计子任务数量
        if (CollectionUtils.isNotEmpty(ids)){
            Query query = Query.select(
                    SqlUtil.wrapperAliasColumn("", SqlUtil.wrapperJsonColumn(FormFieldConstant.PARENT_ID), "parent_id"),
                    SqlUtil.wrapperAliasColumn("", "count(*)", "count")
            ).from(new Table(bizForm.getTableName())).where(Conditions.and(
                    Conditions.in(SqlUtil.wrapperJsonColumn(FormFieldConstant.PARENT_ID), ids)
            )).group(SqlUtil.wrapperJsonColumn(FormFieldConstant.PARENT_ID));
            List<Map<String, Object>> results = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
            if (CollectionUtils.isNotEmpty(results)){
                Map<Object, Object> groups = new HashMap<>();
                for (Map<String, Object> result: results){
                    groups.put(result.get("parent_id"), result.get("count"));
                }
                for (Map<String, Object> data: datas){
                    data.put("childsNum", groups.get(String.valueOf(data.get("id"))));
                }
            }
        }
    }

    private Page<Map<String, Object>> listFilter(Long orgId, Long operatorId, BizForm bizForm, AppValueListReq req){
        Query query = getFilterQuery(orgId, operatorId, bizForm, req);

        if (bizForm.getRelateTablesFieldParams() != null && bizForm.getRelateTablesFieldParams().size() > 0) {
            // 获取数量，因为不需要关联相关逻辑，数量跟没关联是一样的，所以先获取数量
            Long total = 0L;
            if (req.isNeedTotal()) {
                total = getDataTotal(query);
            }
            query = FormRefDsl.getRefDSL(orgId, query, bizForm.getFieldParams(), bizForm.getRelateTablesFieldParams(), req.isAggNoLimit());
            List<Map<String, Object>> list = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
            if (total.equals(0L)) {
                total = (long) list.size();
            }
            // 查询数据
            return new Page<>(total, list);
        } else {
            if (req.isNeedTotal()) {
                return dataCenterProvider.page(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
            } else {
                List<Map<String, Object>> list = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
                return new Page<>((long) list.size(), list);
            }
        }
    }

    public QuerySqlResp querySql(QuerySqlReq querySqlReq, AppValueListReq req) {
        Long orgId = querySqlReq.getOrgId();
        Long operatorId = querySqlReq.getUserId();

        Map<String, FieldParam> fieldParams = goTableService.readFields(req.getTableId(), querySqlReq.getOrgId(), querySqlReq.getUserId());
        BizForm bizForm = new BizForm();
        bizForm.setOrgId(querySqlReq.getOrgId());
        bizForm.setFieldParams(fieldParams);
        bizForm.setExtendsFormId(querySqlReq.getSummaryTableId());
        if (req.getTableId() == null || req.getTableId().equals(0L) || req.getTableId().equals(querySqlReq.getSummaryTableId())) {
            bizForm.setProjectId(-1L);
            bizForm.setAppType(AppType.SUMMARY.getCode());
        }

        if (req.isNeedRefColumn()) {
            bizForm.setRelateTablesFieldParams(getAllRelateTableFields(fieldParams,req.getFilterColumns(),orgId,operatorId));
        }

        Query query = getFilterQuery(orgId, operatorId, bizForm, req);
        if (bizForm.getRelateTablesFieldParams() != null && bizForm.getRelateTablesFieldParams().size() > 0) {
            query = FormRefDsl.getRefDSL(orgId, query, bizForm.getFieldParams(), bizForm.getRelateTablesFieldParams(), req.isAggNoLimit());
        }
        Sql sql = query.toSql();
        String args = JsonUtils.toJson(sql.getArgs());
        QuerySqlResp resp = new QuerySqlResp();
        resp.setSql(sql.getSql());
        resp.setArgs(args);

        return resp;
    }

    private Query getFilterQuery(Long orgId, Long operatorId, BizForm bizForm, AppValueListReq req) {
        // 必要的变量定义
        List<Condition> conditions = new ArrayList<>();
        List<Order> orders = req.getOrders();
//        if (!req.isNeedDeleteData()) {
//            conditions.add(Conditions.equal("delFlag", CommonConsts.FALSE));
//        }
        if (req.getCondition() != null && StringUtils.isNotBlank(req.getCondition().getType())){
            conditions.add(req.getCondition());
        }
        if (CollectionUtils.isEmpty(orders)){
            orders = new ArrayList<>();
        }

        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
        fieldParams.putAll(FormFieldConstant.getCommonFields());

        // order的最后必带个id
//        orders.add(new Order(FormFieldConstant.ID, true, true));
        // 处理条件中的占位符
        String conditionJson = JsonUtils.toJson(conditions);
        if (conditionJson.contains("${")){
            processConditionPlaceholder(conditions.toArray(new Condition[]{}), userCenterService.getPlaceholderContext(orgId, operatorId), fieldParams);
        }
        // 如果是汇总表且project为-1，去掉模板应用数据(projectId为-1，表示北极星的汇总表)
        if (Objects.equals(bizForm.getAppType(), AppType.SUMMARY.getCode()) && Objects.equals(bizForm.getProjectId(), -1L)){
            if (CollectionUtils.isEmpty(req.getRedirectIds()) || req.getRedirectIds().contains(bizForm.getAppId())){
                // 查询模板应用
                List<Long> templateIds = appApi.getTemplateIds(orgId).getData();
                if (CollectionUtils.isNotEmpty(templateIds)){
                    conditions.add(Conditions.notIn(FormFieldConstant.APP_ID, templateIds));
                }
            }
        }

        // 处理字段，包装jsonb字段
        DslUtil.resetReqCondition(conditions);

        // 拼装请求
        int page = req.getPage();
        int size = req.getSize();
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0 || (size > MAX_SIZE && !req.isExport())) {
            size = MAX_SIZE;
        }
        // 处理orders字段
        DslUtil.resetReqOrders(orders, fieldParams, null);

        return Query.select(req.getFilterColumns()).from(new Table(bizForm.getTableName())).where(Conditions.and(conditions)).limit((page - 1) * size, size).orders(orders);
    }

    private Long getDataTotal(Query query) {
        Query countQuery = query.copy();
        countQuery.setColumns(Collections.singletonList("count(*) as count"));
        countQuery.setLimit(null);
        countQuery.setOffset(null);
        countQuery.setOrders(null);

        List<Map<String, Object>> countDatas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), countQuery).getData();
        if(CollectionUtils.isNotEmpty(countDatas)){
            return  Long.parseLong(countDatas.get(0).get("count").toString());
        }

        return 0L;
    }

    private Map<Long, Map<String, FieldParam>> getAllRelateTableFields(Map<String, FieldParam> fieldParams, List<String> filterColumns, Long orgId, Long userId) {
        List<Long> tableIds = new ArrayList<>();
        fieldParams.forEach((fieldName, fieldParam) -> {
            RefSetting setting = fieldParam.getField().getRefSetting();
            if (setting != null && FormRefDsl.checkIsConditionRef(fieldParam.getField().getType())) {
                if (filterColumns == null || FormRefDsl.checkIsInFieldNameList(filterColumns, fieldName)) {
                    tableIds.add(setting.getTableId());
                }
            }
        });
        if (tableIds.size() > 0) {
            List<Long> distinctTableIds = tableIds.stream().distinct().collect(Collectors.toList());
            return goTableService.readTablesFields(distinctTableIds, orgId, userId);
        }

        return new HashMap<>();
    }



    private List<Map<String, Object>> listFilterCustomStat(Long orgId, Long operatorId, BizForm bizForm, AppValueListReq req) {
        String tableName = bizForm.getTableName();
        Map<String, FieldParam> fieldParams = bizForm.getFieldParams();
        fieldParams.putAll(FormFieldConstant.getCommonFields());

        // conditions
        List<Condition> conditions = new ArrayList<>();
        if (req.getCondition() != null && StringUtils.isNotBlank(req.getCondition().getType())) {
            conditions.add(req.getCondition());
        }
        // 处理条件中的占位符
        String conditionJson = JsonUtils.toJson(conditions);
        if (conditionJson.contains("${")) {
            processConditionPlaceholder(conditions.toArray(new Condition[]{}), userCenterService.getPlaceholderContext(orgId, operatorId), fieldParams);
        }
        // 如果是汇总表且project为-1，去掉模板应用数据(projectId为-1，表示北极星的汇总表)
        if (Objects.equals(bizForm.getAppType(), AppType.SUMMARY.getCode()) && Objects.equals(bizForm.getProjectId(), -1L)) {
            if (CollectionUtils.isEmpty(req.getRedirectIds()) || req.getRedirectIds().contains(bizForm.getAppId())) {
                // 查询模板应用
                List<Long> templateIds = appApi.getTemplateIds(orgId).getData();
                if (CollectionUtils.isNotEmpty(templateIds)) {
                    conditions.add(Conditions.notIn(FormFieldConstant.APP_ID, templateIds));
                }
            }
        }
        // 处理字段，包装jsonb字段
        DslUtil.resetReqCondition(conditions);

        int page = req.getPage();
        int size = req.getSize();
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0 || size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        // 拼装请求
        Query query;
        if (CollectionUtils.isEmpty(req.getFilterColumns())) {
            query = Query.select();
        } else {
            query = Query.select(req.getFilterColumns());
        }
        query = query.from(new Table(tableName)).where(Conditions.and(conditions)).limit((page - 1) * size, size);
        if (!CollectionUtils.isEmpty(req.getOrders())) {
            query = query.orders(req.getOrders());
        }
        if (!CollectionUtils.isEmpty(req.getGroups())) {
            query = query.group(req.getGroups());
        }

        // 查询数据
        return dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
    }

    private DataFilterFieldCollector fieldCollect(Map<String, FieldParam> fieldParamMap, List<String> columns) {
        BizForm bizForm = new BizForm();
        bizForm.setFieldParams(fieldParamMap);

        return fieldCollect(bizForm, columns);
    }

    /**
     * 字段收集
     *
     * @param bizForm 字段字典
     * @return {@link DataFilterFieldCollector}
     */
    private DataFilterFieldCollector fieldCollect(BizForm bizForm, List<String> columns) {
        // 不可见的字段
        Set<String> invisibleFields = new HashSet<>();
        // 子表字段
        Set<String> subTableFields = new HashSet<>();
        // 高级关联字段
        Map<String, RelateProInfo> relationProInfos = new HashMap<>();
        // 普通关联字段
        Map<String, RelateTableFieldConfig> relateFieldConfigs = new HashMap<>();
        // 引用字段
        Map<String, QuoteFieldConfig> quoteFieldConfigMap = new HashMap<>();
        // 成员字段
        Set<String> memberFields = new HashSet<>();
        memberFields.add("creator");
        memberFields.add("updator");
        // 部门字段
        Set<String> deptFields = new HashSet<>();

        Map<String,Map<Object, Object>> refOptionFields = new HashMap<>();
        Set<String> refDateFields = new HashSet<>();

        Map<String, FieldParam> fieldParamMap = bizForm.getFieldParams();

        fieldParamMap.forEach((fieldName, fieldParam) -> {
            // 当前查询不包括该字段则不参与计算
            if (CollectionUtils.isNotEmpty(columns) && ! columns.contains(fieldName)){
                return;
            }
            if (!Objects.equals(fieldParam.getInitialVisible(), Boolean.TRUE)) {
                invisibleFields.add(fieldName);
            }else {
                Field field = fieldParam.getField();
                if (Objects.equals(field.getType(), FieldTypeEnums.SUBFORM.getFormFieldType())) {
                    subTableFields.add(fieldName);
                } else if (Objects.equals(field.getType(), FieldTypeEnums.RELATION_TABLE.getFormFieldType())) {
                    Map<String, Object> props = field.getProps();
                    if (props != null && props.containsKey(FieldTypeEnums.RELATION_TABLE.getFormFieldType())) {
                        RelateTableFieldConfig referenceDataFieldConfig = JSON.parseObject(JSON.toJSONString(props.get(FieldTypeEnums.RELATION_TABLE.getFormFieldType())), RelateTableFieldConfig.class);
                        relateFieldConfigs.put(fieldName, referenceDataFieldConfig);
                    }
                } else if (Objects.equals(field.getType(), FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())) {
                    Map<String, Object> props = field.getProps();
                    if (props != null && props.containsKey(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())) {
                        RelateTableProFieldConfig relateProConfig = JSON.parseObject(JSON.toJSONString(props.get(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())), RelateTableProFieldConfig.class);
                        RelateProInfo relateProInfo = new RelateProInfo();
                        relateProInfo.setFieldKey(fieldName);
                        relateProInfo.setAppId(relateProConfig.getAppId());
                        relateProInfo.setLinkShowFieldName(relateProConfig.getLinkShow().getName());
                        relateProInfo.setLinkCondFieldName(relateProConfig.getLinkCond().getName());
                        relateProInfo.setLinkCondFieldType(relateProConfig.getLinkCond().getField().getType());
                        if (relateProConfig.getLinkCond().getField().getDataType() != null){
                            relateProInfo.setLinkCondFieldDataType(relateProConfig.getLinkCond().getField().getDataType().toString());
                        }
                        if (relateProConfig.getCurrentCond() != null ){
                            relateProInfo.setCurrCondFieldType(relateProConfig.getCurrentCond().getField().getType());
                            if (relateProConfig.getCurrentCond().getField().getDataType() != null){
                                relateProInfo.setCurrCondFieldDataType(relateProConfig.getCurrentCond().getField().getDataType().toString());
                            }
                        }
                        // 是否是值类型
                        if (relateProConfig.asValueCond()) {
                            relateProInfo.setValueCond(relateProConfig.getValueCond());
                        } else {
                            relateProInfo.setCurrentCondFieldName(relateProConfig.getCurrentCond().getName());
                        }
                        relationProInfos.put(fieldName, relateProInfo);
                    }
                } else if (Objects.equals(field.getType(), FieldTypeEnums.USER.getFormFieldType())) {
                    memberFields.add(fieldName);
                } else if (Objects.equals(field.getType(), FieldTypeEnums.DEPT.getFormFieldType()) || Objects.equals(field.getType(), FieldTypeEnums.TREE_SELECT.getFormFieldType())) {
                    deptFields.add(fieldName);
                } else if (Objects.equals(field.getType(), FieldTypeEnums.QUOTE_TABLE.getFormFieldType())) {
                    Map<String, Object> props = field.getProps();
                    if (props != null && props.containsKey(FieldTypeEnums.QUOTE_TABLE.getFormFieldType())) {
                        QuoteFieldConfig config = JSON.parseObject(JSON.toJSONString(props.get(FieldTypeEnums.QUOTE_TABLE.getFormFieldType())), QuoteFieldConfig.class);

                        String quoteField = config.getQuoteField();
                        if (StringUtils.isNotBlank(quoteField)){
                            String[] subFields = quoteField.split("\\.");
                            FieldParam beQuoteField = fieldParamMap.get(subFields[0]);
                            if (beQuoteField != null && subFields.length > 1){
                                StringBuilder jsonPath = new StringBuilder("$.");
                                if (Objects.equals(beQuoteField.getField().getType(), FieldTypeEnums.RELATION_TABLE.getFormFieldType())
                                        || Objects.equals(beQuoteField.getField().getType(), FieldTypeEnums.SUBFORM.getFormFieldType())){
                                    jsonPath.append(subFields[0]).append("[*].").append(subFields[1]);
                                    config.setJsonPath(jsonPath.toString());
                                    quoteFieldConfigMap.put(fieldParam.getName(), config);
                                }else if (Objects.equals(beQuoteField.getField().getType(), FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())){
                                    if (Objects.equals(subFields[1], "list") || Objects.equals(subFields[1], "total")){
                                        jsonPath.append(subFields[0]).append(".").append(subFields[1]);
                                    }else{
                                        jsonPath.append(subFields[0]).append(".list[*].").append(subFields[1]);
                                    }
                                    config.setJsonPath(jsonPath.toString());
                                    quoteFieldConfigMap.put(fieldParam.getName(), config);
                                }
                            }
                        }
                    }
                } else if (FormRefDsl.checkIsConditionRef(field.getType())) {
                    RefSetting refSetting = field.getRefSetting();
                    if (refSetting != null && bizForm.getRelateTablesFieldParams() != null) {
                        String relateColumn = refSetting.getColumnId();
                        Map<String, FieldParam> tableFieldParams = bizForm.getRelateTablesFieldParams().get(refSetting.getTableId());
                        if (tableFieldParams != null && tableFieldParams.get(relateColumn) != null) {
                            FieldParam refColumn = tableFieldParams.get(relateColumn);
                            String relateColumnType = refColumn.getField().getType();
                            if (Objects.equals(relateColumnType, FieldTypeEnums.SELECT.getFormFieldType()) ||
                                    Objects.equals(relateColumnType, FieldTypeEnums.MULTISELECT.getFormFieldType()) ||
                                    Objects.equals(relateColumnType, FieldTypeEnums.GROUP_SELECT.getFormFieldType()) ) {

                                Map<Object, Object> options = HeaderUtil.parseSelectOptions(refColumn);
                                refOptionFields.put(fieldParam.getName(), options);
                            } else if (Objects.equals(relateColumnType, FieldTypeEnums.USER.getFormFieldType())) {
                                memberFields.add(fieldName);
                            } else if (Objects.equals(relateColumnType, FieldTypeEnums.DEPT.getFormFieldType())) {
                                deptFields.add(fieldName);
                            } else if (Objects.equals(relateColumnType, FieldTypeEnums.DATE.getFormFieldType())) {
                                refDateFields.add(fieldName);
                            }
                        }
                    }
                }
            }
        });

        DataFilterFieldCollector collector = new DataFilterFieldCollector();
        collector.setInvisibleFields(invisibleFields);
        collector.setRelateFieldConfigs(relateFieldConfigs);
        collector.setRelationProInfos(relationProInfos);
        collector.setQuoteFieldConfigMap(quoteFieldConfigMap);
        collector.setSubTableFields(subTableFields);
        collector.setDeptFields(deptFields);
        collector.setMemberFields(memberFields);
        collector.setFieldParamMap(fieldParamMap);
        collector.setRefOptionFields(refOptionFields);
        collector.setRefDateFields(refDateFields);
        return collector;
    }

    /**
     * 收集资源
     *
     * @param fieldCollector  field收集器
     * @param datas 原数据
     * @return {@link DataFilterResourceCollector}
     */
    private DataFilterResourceCollector resourceCollect(DataFilterFieldCollector fieldCollector, List<Map<String, Object>> datas){
        // 相关资源id
        Set<Long> relevantAppIds = new HashSet<>();
        Set<Long> relevantUserIds = new HashSet<>();
        Set<Long> relevantDeptIds = new HashSet<>();
        Set<Long> relevantRoleIds = new HashSet<>();
        Set<Long> workflowNodeIds = new HashSet<>();
        for (RelateTableFieldConfig c : fieldCollector.getRelateFieldConfigs().values()) {
            relevantAppIds.add(c.getAppId());
        }
        for (RelateProInfo ri : fieldCollector.getRelationProInfos().values()) {
            relevantAppIds.add(ri.getAppId());
        }

        // 普通关联的id字典，key为关联的appId，value为被关联数据的id列表
        Map<RelateInfo, List<Long>> relateDataIdsMap = new HashMap<>();
        // 高级关联的条件数据
        Map<RelateProInfo, List<Object>> relateProConditionDataMap = new HashMap<>();

        DataFilterResourceCollector resourceCollector = new DataFilterResourceCollector();
        resourceCollector.setRelateProConditionDataMap(relateProConditionDataMap);
        resourceCollector.setRelateDataIdsMap(relateDataIdsMap);
        resourceCollector.setRelevantAppIds(relevantAppIds);
        resourceCollector.setRelevantDeptIds(relevantDeptIds);
        resourceCollector.setRelevantRoleIds(relevantRoleIds);
        resourceCollector.setRelevantUserIds(relevantUserIds);
        resourceCollector.setWorkflowNodeIds(workflowNodeIds);

        // 处理数据
        for (Map<String, Object> data : datas) {
            // 移除无效的字段
            for (String invisibleField : fieldCollector.getInvisibleFields()) {
                data.remove(invisibleField);
            }
            // 普通关联数据收集
            for (Map.Entry<String, RelateTableFieldConfig> entry : fieldCollector.getRelateFieldConfigs().entrySet()) {
                Object relateIdsObj = data.get(entry.getKey());
                if (relateIdsObj instanceof Collection) {
                    RelateInfo relateInfo = new RelateInfo();
                    relateInfo.setAppId(entry.getValue().getAppId());
                    relateInfo.setFieldKey(entry.getKey());
                    List<Long> relateIds = relateDataIdsMap.computeIfAbsent(relateInfo, k -> new ArrayList<>());
                    List<Long> currentIds = GsonUtils.readValue(GsonUtils.toJson(relateIdsObj), new TypeToken<List<Long>>() {
                    }.getType());
                    if (CollectionUtils.isNotEmpty(currentIds)){
                        relateIds.addAll(currentIds);
                    }
                }
            }
            // 高级关联条件数据
            for (Map.Entry<String, RelateProInfo> entry : fieldCollector.getRelationProInfos().entrySet()) {
                RelateProInfo relateProInfo = entry.getValue();
                List<Object> relateProDataList = relateProConditionDataMap.getOrDefault(relateProInfo, new ArrayList<>());
                if (relateProInfo.asValueCond()) {
                    // 对于值类型的，只添加一次
                    if (relateProConditionDataMap.containsKey(relateProInfo)) {
                        continue;
                    }
                    Condition valueCond = relateProInfo.getValueCond();
                    if (valueCond.getValue() != null) {
                        relateProDataList.add(valueCond.getValue());
                    }
                } else {
                    Object d = data.get(relateProInfo.getCurrentCondFieldName());
                    if (d != null) {
                        if (d instanceof Collection){
                            relateProDataList.addAll((Collection<?>) d);
                        }else{
                            relateProDataList.add(d);
                        }
                    }
                }
                relateProConditionDataMap.put(relateProInfo, relateProDataList);
            }

            // 收集成员及部门
            parseAndCollectMembers(data, fieldCollector, resourceCollector);

        }
        return resourceCollector;
    }

    private DataFilterDataCollector dataCollect(BizForm bizForm, DataFilterFieldCollector fieldCollector, DataFilterResourceCollector resourceCollector) {

        // 获取普通关联appId的forms, key为appId
//        Map<Long, BizForm> relevantFormMap = MapUtils.toMap(BizForm::getAppId, summaryService.getBizForms(bizForm.getOrgId(), resourceCollector.getRelevantAppIds()));

        // 收集数据
        Map<Long, UserInfoResp> userInfoRespMap = new HashMap<>();
        Map<Long, DeptInfoResp> deptInfoRespMap = new HashMap<>();
        Map<Long, RoleInfoResp> roleInfoRespMap = new HashMap<>();
        // key为appId
        Map<Long, Map<Long, Map<String, Object>>> relateDataMap = new HashMap<>();
        Map<RelateProInfo, List<Map<String, Object>>> relateProDataMap = new HashMap<>();
        Map<String, Map<Object, List<Map<String, Object>>>> subTableDataMap = new HashMap<>();

        // 获取成员相关数据
        if (CollectionUtils.isNotEmpty(resourceCollector.getRelevantUserIds())) {
            userInfoRespMap = userCenterService.getAllUserMap(bizForm.getOrgId(), resourceCollector.getRelevantUserIds());
        }
        if (CollectionUtils.isNotEmpty(resourceCollector.getRelevantDeptIds())) {
            deptInfoRespMap = userCenterService.getAllDeptMap(bizForm.getOrgId(), resourceCollector.getRelevantDeptIds());
        }
        if (CollectionUtils.isNotEmpty(resourceCollector.getRelevantRoleIds())) {
            roleInfoRespMap = userCenterService.getAllRoleMap(bizForm.getOrgId(), resourceCollector.getRelevantRoleIds());
        }

        // key为nodeId
//        Map<Long, NodeTree> workflowNodes = new HashMap<>();
        // 保存关联表，高级关联表以及子表的fieldCollector
//        Map<Long, DataFilterFieldCollector> relateFieldCollectorsMap = new HashMap<>();
//        Map<RelateProInfo, DataFilterFieldCollector> relateProFieldCollectorsMap = new HashMap<>();
//        Map<String, DataFilterFieldCollector> subTableFieldCollectorsMap = new HashMap<>();
//        // 工作流数据
//        Map<Long, NodeTree> nodeTrees = new HashMap<>();
//
//        // 先做多App聚合
//        Map<Long, List<Long>> relateAppDataIdsMap = new HashMap<>(resourceCollector.getRelateDataIdsMap().size());
//        resourceCollector.getRelateDataIdsMap().forEach((k, v) -> {
//            List<Long> ids = relateAppDataIdsMap.computeIfAbsent(k.getAppId(), (o) -> new ArrayList<>());
//            ids.addAll(v);
//        });
//        // 获取普通关联数据
//        for (Map.Entry<Long, List<Long>> entry : relateAppDataIdsMap.entrySet()) {
//            if (entry.getValue().isEmpty()) {
//                continue;
//            }
//            BizForm referenceForm = relevantFormMap.get(entry.getKey());
//            if (referenceForm == null) {
//                continue;
//            }
//            DataFilterFieldCollector relateTableFieldCollector = fieldCollect(referenceForm, null);
//
//            List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
//                    Query.select()
//                            .from(new Table(SqlUtil.wrapperTableName(bizForm.getOrgId(), referenceForm.getFormId())))
//                            .where(
//                                    Conditions.and(
//                                            Conditions.in("id", entry.getValue().toArray()),
//                                            Conditions.equal(SqlUtil.wrapperJsonColumn("status"), CommonConsts.TRUE),
//                                            Conditions.equal(SqlUtil.wrapperJsonColumn("delFlag"), CommonConsts.FALSE)
//                                    )
//                            )
//            ).getData();
//            Map<Long, Map<String, Object>> dataMap = new LinkedHashMap<>();
//            for (Map<String, Object> data : datas) {
//                dataMap.put(Long.valueOf(data.get("id").toString()), data);
//                // 收集成员及部门数据
//                parseAndCollectMembers(data, relateTableFieldCollector, resourceCollector);
//            }
//            relateFieldCollectorsMap.put(entry.getKey(), relateTableFieldCollector);
//            relateDataMap.put(entry.getKey(), dataMap);
//        }
//
//        // 获取高级关联数据
//        for (Map.Entry<RelateProInfo, List<Object>> entry : resourceCollector.getRelateProConditionDataMap().entrySet()) {
//            if (CollectionUtils.isEmpty(entry.getValue())) {
//                continue;
//            }
//            RelateProInfo relateProInfo = entry.getKey();
//            BizForm relateForm = relevantFormMap.get(relateProInfo.getAppId());
//            if (relateForm == null) {
//                continue;
//            }
//            DataFilterFieldCollector relateProTableFieldCollector = fieldCollect(relateForm, null);
//
//            Condition relateCondition;
//            // 为空则不查，不然讲导致sql错误或错误的关联所有数据
//            if (entry.getValue() == null || entry.getValue().isEmpty()) {
//                continue;
//            }
//            // 被关联表中的条件字段如果是关联类型的处理
//            if (FieldTypeEnums.RELATION_TABLE.getFormFieldType().equals(relateProInfo.getLinkCondFieldType()) || Objects.equals(relateProInfo.getLinkCondFieldDataType(), "ARRAY")) {
//                relateCondition = Conditions.valuesIn(SqlUtil.wrapperJsonColumn(relateProInfo.getLinkCondFieldName()), entry.getValue().toArray());
//            } else {
//                relateCondition = Conditions.in(SqlUtil.wrapperJsonColumn(relateProInfo.getLinkCondFieldName()), entry.getValue().toArray());
//            }
//
//            List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
//                    Query.select()
//                            .from(new Table(SqlUtil.wrapperTableName(bizForm.getOrgId(), relateForm.getFormId())))
//                            .where(
//                                    Conditions.and(
//                                            relateCondition,
//                                            Conditions.equal(SqlUtil.wrapperJsonColumn("delFlag"), CommonConsts.FALSE),
//                                            Conditions.equal(SqlUtil.wrapperJsonColumn("status"), CommonConsts.TRUE)
//                                    )
//                            )
//            ).getData();
//            for (Map<String, Object> data : datas) {
//                // 收集成员及部门数据
//                parseAndCollectMembers(data, relateProTableFieldCollector, resourceCollector);
//            }
//            relateProFieldCollectorsMap.put(relateProInfo, relateProTableFieldCollector);
//            relateProDataMap.put(relateProInfo, datas);
//        }
//        // 获取子表数据，默认子表中都是基础字段
//        for(String subTableKey: fieldCollector.getSubTableFields()){
//            String subTableName = SqlUtil.wrapperSubTableName(bizForm.getTableName(), subTableKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);
//            List<Condition> conditions = new ArrayList<>();
//            conditions.add(Conditions.equal(SqlUtil.wrapperJsonColumn("delFlag"), CommonConsts.FALSE));
//            conditions.add(Conditions.in("parent_id", dataIds));
//            if(bizForm.hasExtends()){
//                conditions.add(Conditions.valuesIn(SqlUtil.wrapperJsonColumn(FormFieldConstant.APP_IDS), Collections.singletonList(bizForm.getAppId())));
//            }
//            FieldParam subFieldParam = bizForm.getFieldParams().get(subTableKey);
//            Map<String, FieldParam> subTableParamMap = MapUtils.toMap(FieldParam::getName, subFieldParam.getFields());
//            subTableParamMap.putAll(FormFieldConstant.getCommonFields());
//            DataFilterFieldCollector subTableFieldCollector = fieldCollect(subTableParamMap, null);
//
//            List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
//                    Query.select()
//                            .from(new Table(subTableName))
//                            .where(Conditions.and(conditions))
//            ).getData();
//            Map<Object, List<Map<String, Object>>> parentIdGroup = new LinkedHashMap<>();
//            for(Map<String, Object> data: datas){
//                Object parentId;
//                if((parentId = data.get("parent_id")) != null){
//                    List<Map<String, Object>> groupDatas = parentIdGroup.computeIfAbsent(parentId, k -> new ArrayList<>());
//                    groupDatas.add(data);
//                }
//                // 收集成员及部门数据
//                parseAndCollectMembers(data, subTableFieldCollector, resourceCollector);
//            }
//            subTableFieldCollectorsMap.put(subTableKey, subTableFieldCollector);
//            subTableDataMap.put(subTableKey, parentIdGroup);
//        }
//        // 获取工作流节点数据
//        if (CollectionUtils.isNotEmpty(dataIds) && bizForm.isWorkflow()){
//            nodeTrees = workflowService.getNodesByDataIds(dataIds);
//        }

        DataFilterDataCollector dataCollector = new DataFilterDataCollector();
        dataCollector.setRelateDataMap(relateDataMap);
        dataCollector.setRelateProDataMap(relateProDataMap);
//        dataCollector.setFormMap(relevantFormMap);
        dataCollector.setSubTableDataMap(subTableDataMap);
        dataCollector.setDeptInfoRespMap(deptInfoRespMap);
        dataCollector.setRoleInfoRespMap(roleInfoRespMap);
        dataCollector.setUserInfoRespMap(userInfoRespMap);
//        dataCollector.setNodeTrees(nodeTrees);

//        // 封装关联表成员数据
//        for (Map.Entry<Long, Map<Long, Map<String, Object>>> entry: relateDataMap.entrySet()){
//            DataFilterFieldCollector relateFieldCollector = relateFieldCollectorsMap.get(entry.getKey());
//            for (Map<String, Object> data: entry.getValue().values()){
//                // 装填成员
//                assemblyMembersData(data, relateFieldCollector, dataCollector);
//            }
//        }
//
//        // 封装高级关联表成员数据
//        for (Map.Entry<RelateProInfo, List<Map<String, Object>>> entry: relateProDataMap.entrySet()){
//            DataFilterFieldCollector relateProFieldCollector = relateProFieldCollectorsMap.get(entry.getKey());
//            for (Map<String, Object> data: entry.getValue()){
//                // 装填成员
//                assemblyMembersData(data, relateProFieldCollector, dataCollector);
//            }
//        }
//
//        // 封装子表成员数据
//        for (Map.Entry<String, Map<Object, List<Map<String, Object>>>> entry: subTableDataMap.entrySet()){
//            DataFilterFieldCollector subTableFieldCollector = subTableFieldCollectorsMap.get(entry.getKey());
//            for (List<Map<String, Object>> datas: entry.getValue().values()){
//                for (Map<String, Object> data: datas){
//                    // 装填成员
//                    assemblyMembersData(data, subTableFieldCollector, dataCollector);
//                }
//            }
//        }

        return dataCollector;
    }

    private void dataAssembly(DataFilterFieldCollector fieldCollector, DataFilterDataCollector dataCollector, List<Map<String, Object>> datas){
        // 装填数据
        for(Map<String, Object> data: datas) {
            // 装填成员
            assemblyMembersData(data, fieldCollector, dataCollector);
            assemblyRefData(data, fieldCollector, false);
//            // 装填高级关联数据
//            for (Map.Entry<RelateProInfo, List<Map<String, Object>>> entry : dataCollector.getRelateProDataMap().entrySet()) {
//                if (CollectionUtils.isEmpty(entry.getValue())) {
//                    continue;
//                }
//                RelateProInfo relateProInfo = entry.getKey();
//                Object currentValue;
//                // 值条件
//                if (relateProInfo.asValueCond()) {
//                    currentValue = relateProInfo.getValueCond().getValue();
//                } else {
//                    currentValue = data.get(relateProInfo.getCurrentCondFieldName());
//                }
//                List<Map<String, Object>> destShowDatas = new ArrayList<>();
//                for (Map<String, Object> destData : entry.getValue()) {
//                    Object destValue = destData.get(relateProInfo.getLinkCondFieldName());
//                    if (FieldTypeEnums.RELATION_TABLE.getFormFieldType().equals(relateProInfo.getLinkCondFieldType()) || Objects.equals(relateProInfo.getLinkCondFieldDataType(), "ARRAY")) {
//                        if (Objects.equals(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), relateProInfo.getCurrCondFieldType()) || Objects.equals(relateProInfo.getCurrCondFieldDataType(), "ARRAY")){
//                            if (destValue instanceof Collection && currentValue instanceof Collection && ((Collection<?>) destValue).containsAll(((Collection<?>) currentValue))){
//                                destShowDatas.add(destData);
//                            }
//                        }else{
//                            if (destValue instanceof Collection && ((Collection<?>) destValue).contains(String.valueOf(currentValue))) {
//                                destShowDatas.add(destData);
//                            }
//                        }
//                    } else if (Objects.equals(destValue, currentValue)) {
//                        destShowDatas.add(destData);
//                    }
//                }
//                data.put(relateProInfo.getFieldKey(), new Page<>((long) destShowDatas.size(), destShowDatas));
//            }

//            // 装填普通关联数据
//            for (Map.Entry<String, RelateTableFieldConfig> entry : fieldCollector.getRelateFieldConfigs().entrySet()) {
//                Map<Long, Map<Long, Map<String, Object>>> relateDataMap = dataCollector.getRelateDataMap();
//                Map<Long, Map<String, Object>> relateAppDataMap;
//                if ((relateAppDataMap = relateDataMap.get(entry.getValue().getAppId())) == null) {
//                    continue;
//                }
//                // 原字段值
//                Object relateIdsObj = data.get(entry.getKey());
//                if (relateIdsObj instanceof Collection) {
//                    List<Map<String, Object>> destDataList = new ArrayList<>();
//                    for (Object referenceId : (Collection<?>) relateIdsObj) {
//                        Map<String, Object> destData = relateAppDataMap.get(Long.valueOf(referenceId.toString()));
//                        if (destData != null) {
//                            destDataList.add(destData);
//                        }
//                    }
//                    data.put(entry.getKey(), destDataList);
//                }
//            }
//
//            // 装填子表
//            for (Map.Entry<String, Map<Object, List<Map<String, Object>>>> entry : dataCollector.getSubTableDataMap().entrySet()) {
//                data.put(entry.getKey(), entry.getValue().get(data.get("id")));
//            }

//            // 装填工作流节点
//            if (! MapUtils.isEmpty(dataCollector.getNodeTrees())){
//                data.put(FormFieldConstant.WORKFLOW_NODE_ID, dataCollector.getNodeTrees().get(Long.valueOf(String.valueOf(data.get("id")))));
//            }

//            // 装填引用数据，暂时两层
//            for (Map.Entry<String, QuoteFieldConfig> entry : fieldCollector.getQuoteFieldConfigMap().entrySet()) {
//                QuoteFieldConfig quoteFieldConfig = entry.getValue();
//                Object v = null;
//                DocumentContext documentContext = JsonPath.parse(GsonUtils.toJson(data));
//                try {
//                    Object o = documentContext.read(quoteFieldConfig.getJsonPath());
//                    if (Objects.equals(entry.getValue().getQuoteType(), QuoteType.VALUES.getCode())){
//                        v = o;
//                    }else if (Objects.equals(entry.getValue().getQuoteType(), QuoteType.COUNTALL.getCode())) {
//                        if (o instanceof Collection){
//                            v = ((Collection<?>) o).size();
//                        }else if (o instanceof Map){
//                            v = ((Map<?, ?>) o).size();
//                        }else{
//                            v = 1;
//                        }
//                    }
//                    data.put(entry.getKey(), v);
//                }catch(Exception e){
//                    log.error("装填引用失败 {}", e.getMessage());
//                }
//            }
        }
    }


    /**
     * 处理待办条件，暂定策略当todoType和todoStatus都不为空时才生效
     *
     * @Author Nico
     * @Date 2021/3/31 11:45
     **/
    private Condition processTodosCondition(Long orgId, Long userId, Long appId, Integer todoTye, Integer todoStatus){
        if (Objects.nonNull(todoTye) && Objects.nonNull(todoStatus)){
            List<TodoResp> todoRespList = workflowService.getUserTodos(orgId, userId, appId, null, todoTye, todoStatus);
            if (CollectionUtils.isNotEmpty(todoRespList)){
                return Conditions.in("id", todoRespList.stream().map(TodoResp::getDataId).collect(Collectors.toList()));
            }
        }
        return null;
    }

    /**
     * 处理条件占位符
     *
     * @Author Nico
     * @Date 2021/2/20 14:14
     **/
    private void processConditionPlaceholder(final Condition[] conditions, PlaceholderContext context, Map<String, FieldParam> fieldParams) {
//        log.info("processConditionPlaceholder conditions {}, context {}, fieldparams {}", JsonUtils.toJson(conditions), JsonUtils.toJson(context), JsonUtils.toJson(fieldParams));
        if (Objects.isNull(conditions) || conditions.length == 0) {
            return;
        }

        Long userId = context.getUserId();

        for (Condition condition : conditions) {
            if (condition == null || StringUtils.isBlank(condition.getType())){
                continue;
            }

            switch (condition.getType()){
                case "and": case "or":
                    processConditionPlaceholder(condition.getConds(), context, fieldParams);
                    break;
                case "equal": case "gt": case "gte": case "lt": case "lte": case "un_equal":
                    FieldParam fieldParam = fieldParams.get(condition.getColumn());
                    if (fieldParam != null && Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DATE.getFormFieldType())){
                        handleDatePlaceHolder(condition);
                    }
                    if(condition.getValue() instanceof String && Objects.equals("${current_user}", condition.getValue())){
                        condition.setValue(userId);
                    }else if (condition.getValue() instanceof Collection){
                        List<Object> vs = new ArrayList<>((Collection) condition.getValue());
                        for(int i = 0; i < vs.size(); i ++){
                            Object obj = vs.get(i);
                            if(obj instanceof String){
                                if (((String) obj).contains("${current_user}")){
                                    vs.set(i, ((String) obj).replaceAll("\\$\\{current_user}", String.valueOf(userId)));
                                }
                            }
                        }
                        condition.setValue(vs);
                    }
                    break;
                case "in": case "not_in": case "all_in": case "values_in": case "not_all_in": case "not_values_in":
                    fieldParam = fieldParams.get(condition.getColumn());
                    if (fieldParam == null){
                        continue;
                    }
                    if (condition.getValue() instanceof String && StringUtils.isNotBlank(condition.getValue().toString())){
                        if (((String) condition.getValue()).contains("${current_user}")){
                            if (isUserColumn(condition.getColumn())){
                                condition.setValues(new Object[]{context.getUserId()});
                            }else if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getFormFieldType())) {
                                condition.setValues(new Object[]{((String) condition.getValue()).replaceAll("\\$\\{current_user}", String.valueOf(userId))});
                            }
                        }else if (Objects.equals(condition.getValue(), "${current_dept}")){
                            if (isUserColumn(condition.getColumn())){
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptUserIds())){
                                    condition.setValues(context.getCurrentDeptUserIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getFormFieldType())) {
                                // 成员
                                List<String> values = new ArrayList<>();
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptIds())) {
                                    values.addAll(context.getCurrentDeptIds().stream().map(did -> "D_" + did).collect(Collectors.toList()));
                                }
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptUserIds())){
                                    values.addAll(context.getCurrentDeptUserIds().stream().map(uid -> "U_" + uid).collect(Collectors.toList()));
                                }
                                if (CollectionUtils.isNotEmpty(values)){
                                    condition.setValues(values.toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else{
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptIds())){
                                    condition.setValues(context.getCurrentDeptIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }
                        }else if (Objects.equals(condition.getValue(), "${current_dept_and_childs}")){
                            if (isUserColumn(condition.getColumn())){
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndSubDeptUserIds())){
                                    condition.setValues(context.getCurrentDeptAndSubDeptUserIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getFormFieldType())) {
                                // 成员
                                List<String> values = new ArrayList<>();
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndSubDeptIds())) {
                                    values.addAll(context.getCurrentDeptAndSubDeptIds().stream().map(did -> "D_" + did).collect(Collectors.toList()));
                                }
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndSubDeptUserIds())){
                                    values.addAll(context.getCurrentDeptAndSubDeptUserIds().stream().map(uid -> "U_" + uid).collect(Collectors.toList()));
                                }
                                if (CollectionUtils.isNotEmpty(values)){
                                    condition.setValues(values.toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else{
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndSubDeptIds())){
                                    condition.setValues(context.getCurrentDeptAndSubDeptIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }
                        }if (Objects.equals(condition.getValue(), "${current_dept_and_parents}")){
                            if (isUserColumn(condition.getColumn())){
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndParentDeptIds())){
                                    condition.setValues(context.getCurrentDeptAndSubDeptUserIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getFormFieldType())) {
                                // 成员
                                List<String> values = new ArrayList<>();
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndParentDeptIds())) {
                                    values.addAll(context.getCurrentDeptAndParentDeptIds().stream().map(did -> "D_" + did).collect(Collectors.toList()));
                                }
                                values.add("U_" + context.getUserId());
                                if (CollectionUtils.isNotEmpty(values)){
                                    condition.setValues(values.toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }else{
                                if (CollectionUtils.isNotEmpty(context.getCurrentDeptAndParentDeptIds())){
                                    condition.setValues(context.getCurrentDeptAndParentDeptIds().toArray());
                                }else{
                                    condition.setValues(new Object[]{-1});
                                }
                            }
                        }else if (Objects.equals(condition.getValue(), "${current_user}")){
                            condition.setValues(new Object[]{context.getUserId()});
                        }
                    }else if(ArrayUtils.isNotEmpty(condition.getValues())){
                        for(int index = 0; index < condition.getValues().length; index ++){
                            Object value = condition.getValues()[index];
                            if(value instanceof String){
                                if (((String) value).contains("${current_user}")){
                                    condition.getValues()[index] = ((String) value).replaceAll("\\$\\{current_user}", String.valueOf(userId));
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private static Condition handleDatePlaceHolder(Condition cond){
        if (cond.getValue() instanceof String){
            String type = cond.getType();
            String value = (String) cond.getValue();
            if (value.startsWith("${") && value.endsWith("}")){
                value = value.substring(2, value.length() - 1);
                String[] infos = value.split("[:]");
                String key = infos[0].toLowerCase(Locale.ROOT);
                int n = 0;
                if (infos.length > 1 && StringUtils.isNumeric(infos[1])){
                    n = Integer.parseInt(infos[1]);
                }
                List<String> dates = null;
                if (Objects.equals(key, "today")){
                    dates = DatePlaceHolderUtils.today();
                }else if (Objects.equals(key, "yesterday")){
                    dates = DatePlaceHolderUtils.yesterday();
                }else if (Objects.equals(key, "tomorrow")){
                    dates = DatePlaceHolderUtils.tomorrow();
                }else if (Objects.equals(key, "thisweek")){
                    dates = DatePlaceHolderUtils.thisWeek();
                }else if (Objects.equals(key, "thismonth")){
                    dates = DatePlaceHolderUtils.thisMonth();
                }else if (Objects.equals(key, "lastweek")){
                    dates = DatePlaceHolderUtils.lastWeek();
                }else if (Objects.equals(key, "lastmonth")){
                    dates = DatePlaceHolderUtils.lastMonth();
                }else if (Objects.equals(key, "nextweek")){
                    dates = DatePlaceHolderUtils.nextWeek();
                }else if (Objects.equals(key, "nextmonth")){
                    dates = DatePlaceHolderUtils.nextMonth();
                }else if (Objects.equals(key, "beforeday")){
                    dates = DatePlaceHolderUtils.nDay(-n);
                }else if (Objects.equals(key, "afterday")){
                    dates = DatePlaceHolderUtils.nDay(n);
                }
                if (CollectionUtils.isNotEmpty(dates)){
                    if (Objects.equals(type, Conditions.EQUAL)){
                        cond.setType(Conditions.BETWEEN);
                        cond.setLeft(dates.get(0));
                        cond.setRight(dates.get(1));
                    }else if (Objects.equals(type, Conditions.UN_EQUAL)){
                        cond.setType(Conditions.OR);
                        List<Condition> subConds = new ArrayList<>();
                        subConds.add(Conditions.lt(cond.getColumn(), dates.get(0)));
                        subConds.add(Conditions.gt(cond.getColumn(), dates.get(1)));
                        cond.setConds(subConds.toArray(new Condition[0]));
                    }else if (Objects.equals(type, Conditions.LT)){
                        cond.setValue(dates.get(0));
                    }else if (Objects.equals(type, Conditions.LTE)){
                        cond.setValue(dates.get(1));
                    }else if (Objects.equals(type, Conditions.GT)){
                        cond.setValue(dates.get(1));
                    }else if (Objects.equals(type, Conditions.GTE)){
                        cond.setValue(dates.get(0));
                    }
                }
            }
        }
        return cond;
    }

    private boolean isUserColumn(String key){
        return Objects.equals(key, FormFieldConstant.CREATOR) || Objects.equals(key, FormFieldConstant.UPDATOR);
    }

    /**
     * 解析并收集成员信息
     *
     * @Author Nico
     * @Date 2021/4/20 15:06
     **/
    private void parseAndCollectMembers(Map<String, Object> data, DataFilterFieldCollector fieldCollector, DataFilterResourceCollector resourceCollector) {
        // 收集成员数据
        for (String memberField : fieldCollector.getMemberFields()) {
            Object memberIdsObj = data.get(memberField);
            if (memberIdsObj instanceof Collection) {
                List<String> fieldMemberIds = ((Collection<?>) memberIdsObj).stream().map(String::valueOf).collect(Collectors.toList());
                MemberFieldDatas memberFieldDatas = MemberFieldTypeUtil.parseMemberFieldDataList(fieldMemberIds);
                data.put(memberField, memberFieldDatas);
                resourceCollector.getRelevantUserIds().addAll(memberFieldDatas.getDistinctRealUserIdList());
                resourceCollector.getRelevantDeptIds().addAll(memberFieldDatas.getDistinctRealDeptIdList());
                resourceCollector.getRelevantRoleIds().addAll(memberFieldDatas.getDistinctRealRoleIdList());
            } else if (memberIdsObj != null) {
                try {
                    resourceCollector.getRelevantUserIds().add(Long.valueOf(String.valueOf(memberIdsObj)));
                } catch (Exception e) {
                    log.warn("invalid member data: {}", data);
                }
            }
        }
        // 收集部门
        for (String deptField : fieldCollector.getDeptFields()) {
            Object deptIdsObj = data.get(deptField);
            if (deptIdsObj instanceof Collection) {
                List<Long> fieldDeptIds = ((Collection<?>) deptIdsObj).stream().map(id -> {
                    if (id == null) {
                        return 0L;
                    }
                    return Long.valueOf(String.valueOf(id));
                }).collect(Collectors.toList());
                fieldDeptIds.forEach(id -> {
                    if (id != 0) {
                        resourceCollector.getRelevantDeptIds().add(id);
                    }
                });
            }
        }
    }

    /**
     * 装填成员数据
     *
     * @Author Nico
     * @Date 2021/4/20 15:06
     **/
    private void assemblyMembersData(Map<String, Object> data, DataFilterFieldCollector fieldCollector, DataFilterDataCollector dataCollector){
        // 装填成员
        for (String memberField : fieldCollector.getMemberFields()) {
            Object memberObj = data.get(memberField);
            if (memberObj instanceof MemberFieldDatas) {
                MemberFieldDatas memberFieldDatas = (MemberFieldDatas) memberObj;
                List<Member> members = new ArrayList<>();
                for(MemberFieldData item: memberFieldDatas.getDataList()){
                    Member member = null;
                    if (Objects.equals(item.getType(), FormConstant.MEMBER_USER_PREFIX)) {
                        UserInfoResp resp = dataCollector.getUserInfoRespMap().get(item.getRealId());
                        if (Objects.nonNull(resp)) {
                            member = new Member(resp.getId(), resp.getName(), resp.getAvatar(), FormConstant.MEMBER_USER_PREFIX, resp.getStatus(), resp.getIsDelete());
                        }
                    } else if (Objects.equals(item.getType(), FormConstant.MEMBER_DEPT_PREFIX)) {
                        DeptInfoResp resp = dataCollector.getDeptInfoRespMap().get(item.getRealId());
                        if (Objects.nonNull(resp)) {
                            member = new Member(resp.getId(), resp.getName(), "", FormConstant.MEMBER_DEPT_PREFIX, resp.getStatus(), resp.getIsDelete());
                        }
                    } else if (Objects.equals(item.getType(), FormConstant.MEMBER_ROLE_PREFIX)) {
                        RoleInfoResp resp = dataCollector.getRoleInfoRespMap().get(item.getRealId());
                        if (Objects.nonNull(resp)) {
                            member = new Member(resp.getId(), resp.getName(), "", FormConstant.MEMBER_ROLE_PREFIX, resp.getStatus(), resp.getIsDelete());
                        }
                    }
                    if(Objects.nonNull(member)){
                        members.add(member);
                    }
                }
                data.put(memberField, members);
            } else if (memberObj != null) {
                try {
                    UserInfoResp resp = dataCollector.getUserInfoRespMap().get(Long.valueOf(String.valueOf(memberObj)));
                    if (resp != null){
                        data.put(memberField, new Member(resp.getId(), resp.getName(), resp.getAvatar(), FormConstant.MEMBER_USER_PREFIX, resp.getStatus(), resp.getIsDelete()));
                    }
                } catch (Exception e) {
                    log.warn("invalid member data: {}", data);
                }
            }
        }

        // 装填部门
        for (String deptField: fieldCollector.getDeptFields()){
            Object deptIdsObj = data.get(deptField);
            if(deptIdsObj instanceof Collection){
                List<Member> members = new ArrayList<>();
                for(Object deptId: (Collection<?>) deptIdsObj){
                    DeptInfoResp resp = dataCollector.getDeptInfoRespMap().get(Long.valueOf(String.valueOf(deptId)));
                    if (Objects.nonNull(resp)) {
                        Member member = new Member(resp.getId(), resp.getName(), "", FormConstant.MEMBER_DEPT_PREFIX, resp.getStatus(), resp.getIsDelete());
                        members.add(member);
                    }
                }
                data.put(deptField, members);
            }
        }
    }

    private void assemblyRefData(Map<String, Object> data, DataFilterFieldCollector fieldCollector, boolean needSelectValue) {
        if (needSelectValue) {
            fieldCollector.getRefOptionFields().forEach((fieldName, options) -> {
                Object optionObject = data.get(fieldName);
                if (optionObject instanceof Collection) {
                    List<Object> newValues = new ArrayList<>();
                    for(Object optionId: (Collection<?>) optionObject){
                        if (optionId == null) {
                            continue;
                        }
                        if (options.get(optionId) != null) {
                            newValues.add(options.get(optionId));
                        } else if (StringUtils.isNumeric(optionId.toString())) {
                            Integer interOptionId = Integer.parseInt(optionId.toString());
                            if (options.get(interOptionId) != null) {
                                newValues.add(options.get(interOptionId));
                            }
                        }
                    }
                    data.put(fieldName, newValues);
                }
            });
        }

        fieldCollector.getRefDateFields().forEach(fieldName -> {
            Object valueObject = data.get(fieldName);
            if (valueObject instanceof Collection) {
                List<Object> newValues = new ArrayList<>();
                for(Object value: (Collection<?>) valueObject){
                    if (value != null && !Objects.equals(value, "1970-01-01 00:00:00") && !Objects.equals(value, "0001-01-01 00:00:00")) {
                        newValues.add(value);
                    }
                }
                data.put(fieldName, newValues);
            }
        });
    }


}
