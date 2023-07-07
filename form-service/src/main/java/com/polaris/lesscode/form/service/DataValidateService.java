package com.polaris.lesscode.form.service;

import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.SubTableDatas;
import com.polaris.lesscode.form.bo.TableDatas;
import com.polaris.lesscode.form.config.ValidatorContext;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.handler.*;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.validator.ValidateError;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.uc.internal.resp.DeptInfoResp;
import com.polaris.lesscode.uc.internal.resp.RoleInfoResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 数据保存服务，提供简单的功能
 *
 * @author Nico
 * @date 2021-02-21 
 */
@Slf4j
@Service
public class DataValidateService {

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private UserCenterService userCenterService;

    /**
     * 校验唯一性
     *
     * @param uniques      唯一性检测的目标值，{tableName: {fieldName: [{数据},{数据}]}}
     * @param hasPreHandle 是否存在预处理
     * @param excludeIds 需要跳过的ids
     * @Author Nico
     * @Date 2021/2/4 14:00
     **/
    public void validateUnique(Map<String, Map<String, Map<Long, List<Object>>>> uniques, boolean hasPreHandle, List<Long> excludeIds) {
        if (MapUtils.isNotEmpty(uniques)) {
            Query query = null;
            for (Map.Entry<String, Map<String, Map<Long, List<Object>>>> formData : uniques.entrySet()) {
                String tableName = formData.getKey();
                List<Condition> conds = new ArrayList<>();
                if (MapUtils.isNotEmpty(formData.getValue())) {
                    for (Map.Entry<String, Map<Long, List<Object>>> datas : formData.getValue().entrySet()) {
                        if(MapUtils.isNotEmpty(datas.getValue())){
                            datas.getValue().forEach((dataId, dataValues) -> {
                                if (CollectionUtils.isNotEmpty(dataValues)){
                                    List<Condition> subConds = new ArrayList<>();
                                    if (! hasPreHandle) {
                                        subConds.add(Conditions.in(SqlUtil.wrapperJsonColumn(datas.getKey()), dataValues.toArray()));
                                    } else {
                                        for (Object v : dataValues) {
                                            subConds.add(Conditions.like(SqlUtil.wrapperJsonColumn(datas.getKey()), "%" + v + "%"));
                                        }
                                    }
                                    if(dataId != null && dataId > 0){
                                        subConds.add(Conditions.equal("parent_id", dataId));
                                    }
                                    conds.add(Conditions.and(subConds));
                                }
                            });
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(conds)) {
                    Query currentQuery = Query
                            .select("id")
                            .from(new Table(tableName))
                            .limit(0, 1);
                    if (CollectionUtils.isEmpty(excludeIds)) {
                        currentQuery.where(Conditions.and(Conditions.or(conds)));
                    } else {
                        currentQuery.where(Conditions.and(Conditions.or(conds), Conditions.notIn("id", excludeIds.toArray(new Object[0]))));
                    }
                    if (query == null) {
                        query = currentQuery;
                    } else {
                        query.unionAll(currentQuery);
                    }
                }
            }
            if (query != null) {
                List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), query).getData();
                if (CollectionUtils.isNotEmpty(datas)) {
                    throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), String.format(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getMessage(), "唯一字段值不允许重复插入"));
                }
            }
        }
    }

    /**
     * 验证成员数据
     *
     * @param orgId 组织id
     * @param userIdMap 用户
     * @param roleIdMap 角色
     * @param deptIdMap 部门
     * @throws BusinessException {@link ResultCode#FIELD_VALUE_VALIDATE_ERROR}
     */
    public void validateMembers(Long orgId, Map<Long, String> userIdMap, Map<Long, String> roleIdMap, Map<Long, String> deptIdMap) throws BusinessException {
        if (!userIdMap.isEmpty()) {
            List<Long> validateUserIds = userIdMap.keySet().stream().filter(id -> id > 0).collect(Collectors.toList());
            Map<Long, UserInfoResp> userMap = userCenterService.getAllUserMap(orgId, validateUserIds);
            if (userMap.size() < validateUserIds.size()) {
                throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), String.format(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getMessage(), "存在无效成员"));
            }
        }

        if (!roleIdMap.isEmpty()) {
            Map<Long, RoleInfoResp> roleMap = userCenterService.getAllRoleMap(orgId, roleIdMap.keySet());
            if (roleMap.size() < roleIdMap.size()) {
                throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), String.format(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getMessage(), "存在无效角色"));
            }
        }

        if (!deptIdMap.isEmpty()) {
            Map<Long, DeptInfoResp> deptMap = userCenterService.getAllDeptMap(orgId, deptIdMap.keySet());
            if (deptMap.size() < deptIdMap.size()) {
                throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), String.format(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getMessage(), "存在无效部门"));
            }
        }
    }

    /**
     * 验证部门数据
     *
     * @param orgId 组织id
     * @param deptIds 部门id列表
     * @throws BusinessException {@link ResultCode#FIELD_VALUE_VALIDATE_ERROR}
     */
    public void validateDeprtments(Long orgId, Collection<Long> deptIds) throws BusinessException {
        if (!deptIds.isEmpty()) {
            Map<Long, DeptInfoResp> deptMap = userCenterService.getAllDeptMap(orgId, deptIds);
            if (deptMap.size() < deptIds.size()) {
                throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), String.format(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getMessage(), "存在无效部门"));
            }
        }
    }

    public void validateTableData(List<Map<String, Object>> datas, Map<String, FieldParam> fieldParams, ValidateDataCaller preValidateCall, ValidateErrorCallBack validateErrorCallBack, ValidateFilterFun afterValidateCall, ValidateSucCallBackFun validateSucCallBack) {
        _validateTableData(null, null, datas, fieldParams, preValidateCall, validateErrorCallBack, afterValidateCall, validateSucCallBack);
    }

    private void _validateTableData(Long dataId, String subformKey, List<Map<String, Object>> datas, Map<String, FieldParam> fieldParams, ValidateDataCaller preValidateCall, ValidateErrorCallBack validateErrorCallBack, ValidateFilterFun afterValidateCall, ValidateSucCallBackFun validateSucCallBack) {
        if (CollectionUtils.isNotEmpty(datas)) {
            for (Map<String, Object> data : datas) {
                if(preValidateCall != null){
                    preValidateCall.call(data, fieldParams);
                }
                if (data.containsKey("id")){
                    dataId = Long.valueOf(String.valueOf(data.get("id")));
                }
                Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator();
                List<SubTableDatas> subTableDatas = new ArrayList<>();
                while (it.hasNext()) {
                    Map.Entry<String, Object> kv = it.next();
                    String key = kv.getKey();
//                    if (FormFieldConstant.UN_VALIDATE_EXCLUDED_FIELDS.contains(key)){
//                        continue;
//                    }
                    Object value = kv.getValue();
                    FieldParam fieldParam = fieldParams.get(key);
                    if (fieldParam == null) {  // 表头未定义
                        continue;
                    }
                    if (fieldParam.isSubForm() && CollectionUtils.isNotEmpty(fieldParam.getFields())) {
                        if (!(value instanceof List)) {  // 子表单数据不是列表
                            continue;
                        }
                        Map<String, FieldParam> subFieldParamMap = fieldParam.getFields().stream().collect(Collectors.toMap(FieldParam::getName, subFieldParam -> subFieldParam));
                        List<Map<String, Object>> subDatas = (List<Map<String, Object>>) value;
                        _validateTableData(dataId, key, subDatas, subFieldParamMap, preValidateCall, validateErrorCallBack, afterValidateCall, validateSucCallBack);
                        subTableDatas.add(new SubTableDatas(key, subDatas));
                        // 移除掉子表单数据
                        it.remove();
                    } else if (! FormFieldConstant.getCommonFields().containsKey(fieldParam.getName()) || FormFieldConstant.NEED_DEAL_RELATING_TYPE.contains(fieldParam.getField().getType())){
                        // 各字段单独校验
                        try {
                            ValidatorContext.validate(FieldTypeEnums.formatByFieldType(fieldParam.getField().getType()), fieldParam, value);
                        } catch (ValidateError e) {
                            if (validateErrorCallBack != null){
                                validateErrorCallBack.handle(fieldParam, it, data, e);
                            }else{
                                throw new BusinessException(ResultCode.FIELD_VALUE_VALIDATE_ERROR.getCode(), e.getMessage());
                            }
                        }
                    }
                    if(afterValidateCall != null){
                        afterValidateCall.filter(StringUtils.isBlank(subformKey) ? null : dataId, subformKey, data, fieldParam);
                    }
                }
                if (StringUtils.isBlank(subformKey)) {
                    validateSucCallBack.handle(fieldParams, new TableDatas(data, subTableDatas));
                }
            }
        }
    }

    public void assemblyDefaultValue(Map<String, Object> data, Map<String, FieldParam> fieldParams, Long userId){
        for(Map.Entry<String, FieldParam> entry: fieldParams.entrySet()){
            if(! FormFieldConstant.isCommonField(entry.getKey()) && ! Objects.equals(entry.getKey(), FormFieldConstant.ID)){
                Map<String, Object> props = entry.getValue().getField().getProps();
                if (MapUtils.isNotEmpty(props) && props.containsKey("defaultValue")){
                    data.putIfAbsent(entry.getKey(), processDefaultValuePlaceholder(entry.getValue(), props.get("defaultValue"), userId));
                }
                data.putIfAbsent(entry.getKey(), null);
            }
        }
    }

    public void assemblyDefaultValue(List<Map<String, Object>> datas, Map<String, FieldParam> fieldParams, Long userId){
        for(Map<String, Object> data: datas){
            assemblyDefaultValue(data, fieldParams, userId);
        }
    }

    public static Object processDefaultValuePlaceholder(FieldParam param, Object defaultValue, Long userId) {
        if (Objects.isNull(defaultValue)){
            return null;
        }
        if (Objects.equals(param.getField().getType(), FieldTypeEnums.USER.getFormFieldType())){
            if (defaultValue instanceof List){
                List<Object> values = (List<Object>)defaultValue;
                // 数组里包括成员，说明是
                for (int index = 0; index < values.size(); index ++){
                    Object v = values.get(index);
                    if (v instanceof String){
                        if (v.equals("${current_user}")){
                            values.set(index, "U_" + userId);
                        }else if (((String) v).contains("${current_user}")){
                            values.set(index, ((String) v).replaceAll("\\$\\{current_user}", String.valueOf(userId)));
                        }
                    }
                }
            }
        }
        return defaultValue;
    }

}
