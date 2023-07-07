package com.polaris.lesscode.form.service;

import com.polaris.lesscode.form.bo.CollaboratorRelation;
import com.polaris.lesscode.form.bo.CollaboratorColumnUser;
import com.polaris.lesscode.form.constant.FormConstant;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DataCollaboratorService {

    @Autowired
    private DataValidateService dataValidateService;

    @Autowired
    private GoTableService goTableService;

    public Map<String, Object> getCreateCollaborators(Map<String,Object> row, Map<String,FieldParam> currentFieldParams) {
        Map<String, Object> allCollaborators = new HashMap<>();
        currentFieldParams.forEach((s, fieldParam) -> {
            Object value = row.get(fieldParam.getName());
            Object ids = getCollaboratorIds(fieldParam, value);
            if (ids != null) {
                allCollaborators.put(fieldParam.getName(), ids);
            }
        });

        return allCollaborators;
    }

    public Map<String, Object> getUpdateCollaborators(Map<String,Object> newRow, Map<String,Object> oldRow,
                                                      Map<String,FieldParam> currentFieldParams) {
        Map<String, Object> allCollaborators = new HashMap<>();
        currentFieldParams.forEach((s1, fieldParam) -> {
            String fieldName = fieldParam.getName();
            Object value;
            if (newRow.containsKey(fieldName)) {
                value = newRow.get(fieldName);
            } else {
                value = oldRow.get(fieldName);
            }
            // 获取协作人ids
            Object ids = getCollaboratorIds(fieldParam, value);
            if (ids != null) {
                allCollaborators.put(fieldName, ids);
            }
        });

        return allCollaborators;
    }

    public List<CollaboratorColumnUser> handleDataAdd(Long orgId, Long userId, Long appId, Long tableId, List<Map<String, Object>> rows,
                                  Map<String, FieldParam> fieldParams, boolean isRequest) {
        return getRowsCollaboratorUsers(orgId, appId, tableId, rows, fieldParams);
    }

    public List<CollaboratorColumnUser> handleDataDelete(Long orgId, Long userId, Long appId, Long tableId, List<Map<String, Object>> rows,
                              Map<String, FieldParam> fieldParams, boolean isRequest) {
        return getRowsCollaboratorUsers(orgId, appId,tableId, rows, fieldParams);
    }

    public void handleDataUpdate(Long orgId, Long userId, Long newAppId, Long newTableId, Map<String, Object> newRow, Map<String, Object> oldRow,
                                 Map<String, FieldParam> newFieldParams, AtomicReference<Map<String, FieldParam>> oldFieldParams, CollaboratorRelation collaboratorRelation) {

        Long oldTableId = 0L;
        if (oldRow.get(FormFieldConstant.TABLE_ID) != null && !Objects.equals(String.valueOf(oldRow.get(FormFieldConstant.TABLE_ID)), "")) {
            oldTableId = Long.valueOf(String.valueOf(oldRow.get(FormFieldConstant.TABLE_ID)));
        }

        // 如果都是tableId为0，则协作人其实是没有意义的，直接返回
        if (newTableId.equals(0L) && oldTableId.equals(0L)) {
            return ;
        }

        Long oldAppId = getAppIdFromRow(oldRow);
        if (oldFieldParams.get() == null && !newTableId.equals(oldTableId)) {
            if (oldTableId.equals(0L)) {
                oldFieldParams.set(newFieldParams);
            } else {
                oldFieldParams.set(goTableService.readFields(oldTableId, orgId, userId));
            }
        }

        // 如果两个tableId不一致，则证明是数据移动到另一个表了，老的需要删除，新的需要增加
        if (!newTableId.equals(oldTableId)) {
            List<Map<String, Object>> rows = Collections.singletonList(oldRow);
            if (!newTableId.equals(0L)) {
                List<CollaboratorColumnUser> users = handleDataAdd(orgId, userId, newAppId, newTableId, rows, newFieldParams, false);
                collaboratorRelation.addUser(users);

            }
            if (!oldTableId.equals(0L)) {
                List<CollaboratorColumnUser> users = handleDataDelete(orgId, userId, oldAppId, oldTableId, rows, oldFieldParams.get(), false);
                collaboratorRelation.deleteUser(users);
            }

            return ;
        }

        // 正常的更新数据，表id没有变化
        newFieldParams.forEach((fieldName, fieldParam) -> {
            if (newRow.get(fieldName) != null) {
                Object newIds = getMemberFieldIds(fieldParam, newRow.get(fieldName));
                Object oldIds = getMemberFieldIds(fieldParam, oldRow.get(fieldName));

                Set<String> newIdsMap = new HashSet<>(objToList(newIds));
                Set<String> oldIdsMap = new HashSet<>(objToList(oldIds));
                // 新的不在老的里面，证明是新增
                newIdsMap.forEach(id ->{
                    if (!oldIdsMap.contains(id)) {
                        CollaboratorColumnUser user = newCollaboratorUser(orgId, newAppId, newTableId, fieldParam, id);
                        if (user != null) {
                            collaboratorRelation.addUser(user);
                        }
                    }
                });
                // 老的不在新的里面，则证明是删除
                oldIdsMap.forEach(id ->{
                    if (!newIdsMap.contains(id)) {
                        CollaboratorColumnUser user = newCollaboratorUser(orgId, newAppId, newTableId, fieldParam, id);
                        if (user != null) {
                            collaboratorRelation.deleteUser(user);
                        }
                    }
                });
            }
        });
    }

    private Long getAppIdFromRow(Map<String, Object> row) {
        if (row.get(FormFieldConstant.APP_ID) != null) {
            return Long.valueOf(row.get(FormFieldConstant.APP_ID).toString());
        }
        return 0L;
    }


    private List<String> objToList(Object obj) {
        List<String> result = new ArrayList<>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                if (Objects.isNull(o)) {
                    continue;
                }
                result.add((String) o);
            }
        }
        return result;
    }

    private List<CollaboratorColumnUser> getRowsCollaboratorUsers(Long orgId, Long appId, Long tableId, List<Map<String, Object>> rows,
                                                                  Map<String, FieldParam> fieldParams) {

        List<CollaboratorColumnUser> users = new ArrayList<>();
        fieldParams.forEach((fieldName, fieldParam) -> {
            rows.forEach(row -> {
                List<CollaboratorColumnUser> fieldUsers = getFieldCollaboratorUsers(orgId, appId, tableId, fieldParam, row.get(fieldName));
                users.addAll(fieldUsers);
            });
        });

        return users;
    }

    private List<CollaboratorColumnUser> getFieldCollaboratorUsers(Long orgId,Long appId, Long tableId, FieldParam fieldParam,
                                                       Object FieldData) {

        List<CollaboratorColumnUser> users = new ArrayList<>();
        Object ids = getMemberFieldIds(fieldParam, FieldData);
        if (ids instanceof ArrayList<?>) {
            for (Object id : (List<?>) ids) {
                if (Objects.isNull(id)) {
                    continue;
                }
                CollaboratorColumnUser user =  newCollaboratorUser(orgId, appId, tableId,fieldParam,  (String) id);
                if (user != null) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    private CollaboratorColumnUser newCollaboratorUser(Long orgId, Long appId, Long tableId, FieldParam fieldParam, String idStr) {
        CollaboratorColumnUser user = new CollaboratorColumnUser(appId, orgId, 0L, tableId, fieldParam.getName(),0L);
        if (idStr.contains(FormConstant.MEMBER_USER_PREFIX)) {
            idStr = idStr.replaceFirst(FormConstant.MEMBER_USER_PREFIX, "");
            user.setUserId(Long.valueOf(idStr));
            return user;
        } else if (idStr.contains(FormConstant.MEMBER_DEPT_PREFIX)) {
            idStr = idStr.replaceFirst(FormConstant.MEMBER_DEPT_PREFIX, "");
            user.setDeptId(Long.valueOf(idStr));
            return user;
        }

        return null;
    }

    public boolean judgeHasCollaboratorRoles(FieldParam fieldParam) {
        Map<String, Object> props = fieldParam.getField().getProps();
        if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getDataType()) || Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getDataType())){
            if (MapUtils.isNotEmpty(props) && props.containsKey("collaboratorRoles")){
                Object collaboratorRolesObj = props.get("collaboratorRoles");
                if (collaboratorRolesObj instanceof Collection && CollectionUtils.isNotEmpty((Collection<?>) collaboratorRolesObj)){
                    return true;
                }
            }
        }

        return false;
    }

    // 如果字段开启了协作人，则会返回该字段对应的用户id列表
    public Object getCollaboratorIds(FieldParam fieldParam, Object value) {
        if (value == null) {
            return null;
        }

        if (FormFieldConstant.RECORD_COLLABORATOR_TYPES.contains(fieldParam.getField().getType())) {
            String prefix = null;
            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getFormFieldType())) {
                prefix = FormConstant.MEMBER_DEPT_PREFIX;
            }
            return getCollaboratorIdsFromValue(fieldParam, value, prefix);
        }

        return null;
    }

    // 获取指定member相关的id列表，用于记录协作人关系，为了避免关闭和开启协作人的时候大批量刷新数据，所以只要有协作人类型的字段都记录，不关心开启不开启
    public Object getMemberFieldIds(FieldParam fieldParam, Object value) {
        if (value == null) {
            return null;
        }

        if (checkIsRecordCollaborator(fieldParam)) {
            String prefix = null;
            if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getFormFieldType())) {
                prefix = FormConstant.MEMBER_DEPT_PREFIX;
            }
            return getFormValueIds(value, prefix);
        }

        return null;
    }

    public boolean checkIsRecordCollaborator(FieldParam fieldParam) {
       return FormFieldConstant.RECORD_COLLABORATOR_TYPES.contains(fieldParam.getField().getType()) && !FormFieldConstant.UN_RECORD_COLLABORATOR_FIELDS.contains(fieldParam.getName());
    }

    public Object getCollaboratorIdsFromValue(FieldParam fieldParam, Object value, String prefix) {
        if (judgeHasCollaboratorRoles(fieldParam)) {
            return getFormValueIds(value, prefix);
        }
        return null;
    }

    public Object getFormValueIds(Object value, String prefix) {
        if(value instanceof Collection){
            if (null == prefix || "".equals(prefix)) {
                return value;
            }

            // 需要拼接前缀的
            List<String> members = new ArrayList<>();
            ((Collection<?>)value).forEach(item -> {
                if (item instanceof String) {
                    String itemStr = (String) item;
                    if (itemStr.contains(prefix)) {
                        members.add(itemStr);
                    } else {
                        members.add(prefix + item);
                    }
                } else {
                    members.add(prefix + item);
                }
            });
            return members;
        } else if (value instanceof Map) {
            Map m = (Map) value;
            if (m.containsKey(FormFieldConstant.COLLABORATOR_IDS)) {
                Object ids = m.get(FormFieldConstant.COLLABORATOR_IDS);
                if (ids instanceof Collection) {
                    return ids;
                }
            }
        }

        return null;
    }
}
