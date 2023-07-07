package com.polaris.lesscode.form.constant;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.Column;
import com.polaris.lesscode.form.internal.sula.FieldParam;

import java.util.*;

/**
 * 表单字段定义（暂时驼峰，之后改为下滑杠）
 *
 * @Author Nico
 * @Date 2021/2/22 11:02
 **/
public class FormFieldConstant {

    /**
     * 公共字段定义
     **/
    public final static String CREATOR = "creator";
    public final static String CREATE_TIME = "createTime";
    public final static String UPDATOR = "updator";
    public final static String UPDATE_TIME = "updateTime";
    public final static String STATUS = "status";
    public final static String RECYCLE_FLAG = "recycleFlag";
    public final static String RECYCLE_TIME = "recycleTime";
//    public final static String DEL_FLAG = "delFlag";
//    public final static String APP_IDS = "appIds";
    public final static String APP_ID = "appId";
    public final static String PROJECT_ID = "projectId";
    public final static String ORDER = "order";
    public final static String WORKFLOW_NODE_ID = "workflow_node";
    public final static String AUDITOR_IDS = "auditorIds";
    public final static String PATH = "path";
    public final static String CODE = "code";
    public final static String DATA = "data";

    public final static String ID = "id";
    public final static String ORG_ID = "orgId";
    public final static String PARENT_ID = "parentId";
    public final static String ISSUE_ID = "issueId";
    public final static String TABLE_ID = "tableId";

    // 关联 前后置
    public final static String RELATING = "relating";
    public final static String BA_RELATING = "baRelating";

    // 协作人
    public final static String COLLABORATORS = "collaborators";

    // 工时
    public final static String WORK_HOUR = "workHour";

    // 协作人列表，用于继承了协作人逻辑的子key，因为目前协作人的列key对应的值不一定是id数组了，需要额外信息，所以有一个子key存储
    public final static String COLLABORATOR_IDS = "collaboratorIds";

    // 公共字段
    private final static Map<String, FieldParam> COMMON_FIELDS = new LinkedHashMap<>();
    private final static Map<String, Column> COMMON_COLUMNS = new LinkedHashMap<>();

    // 特定场景需要排除的字段
    private final static Set<String> INSERT_EXCLUDED_FIELDS = new HashSet<>();
    private final static Set<String> UPDATE_EXCLUDED_FIELDS = new HashSet<>();
    private final static Set<String> SELECT_EXCLUDED_FIELDS = new HashSet<>();
    private final static Set<String> SUPPORTED_IMPORT_FIELD_TYPES = new HashSet<>();
    public final static Set<String> UN_VALIDATE_EXCLUDED_FIELDS = new HashSet<>();
    public final static Set<String> UN_RECORD_COLLABORATOR_FIELDS = new HashSet<>(); // 排除记录协作人关联关系的列
    public final static Set<String> RECORD_COLLABORATOR_TYPES = new HashSet<>(); // 记录协作人关联关系的类型
    public final static Set<String> NEED_DEAL_RELATING_TYPE = new HashSet<>(); // 那些类型的字段需要处理数据的关联关系

    // 公共字段装填
    static {
        COMMON_FIELDS.put(CREATOR, FieldParam.buildSystem(CREATOR, "创建人", FieldTypeEnums.USER));
        COMMON_FIELDS.put(CREATE_TIME, FieldParam.buildSystem(CREATE_TIME, "创建时间", FieldTypeEnums.DATE));
        COMMON_FIELDS.put(UPDATOR, FieldParam.buildSystem(UPDATOR, "更新人", FieldTypeEnums.USER));
        COMMON_FIELDS.put(UPDATE_TIME, FieldParam.buildSystem(UPDATE_TIME, "更新时间", FieldTypeEnums.DATE));
        COMMON_FIELDS.put(STATUS, FieldParam.buildSystem(STATUS, "启用状态", FieldTypeEnums.STATUS));
//        COMMON_FIELDS.put(APP_ID, FieldParam.buildSystem(APP_ID, "子应用ID", FieldTypeEnums.NUMBER));
        COMMON_FIELDS.put(PARENT_ID, FieldParam.buildSystem(PARENT_ID, "数据ID", FieldTypeEnums.SINGLE_TEXT));
//        COMMON_FIELDS.put(RECYCLE_FLAG, FieldParam.build(RECYCLE_FLAG, "回收标记", FieldTypeEnums.RECYCLE_FLAG));
//        COMMON_FIELDS.put(RECYCLE_TIME, FieldParam.build(RECYCLE_TIME, "回收时间", FieldTypeEnums.DATE));

        COMMON_FIELDS.put(RELATING, FieldParam.buildSystem(RELATING, "关联", FieldTypeEnums.RELATING));
        COMMON_FIELDS.put(BA_RELATING, FieldParam.buildSystem(BA_RELATING, "前后置", FieldTypeEnums.BA_RELATING));

        COMMON_COLUMNS.put(CREATOR, new Column(COMMON_FIELDS.get(CREATOR)));
        COMMON_COLUMNS.put(CREATE_TIME, new Column(COMMON_FIELDS.get(CREATE_TIME)));
        COMMON_COLUMNS.put(UPDATOR, new Column(COMMON_FIELDS.get(UPDATOR)));
        COMMON_COLUMNS.put(UPDATE_TIME, new Column(COMMON_FIELDS.get(UPDATE_TIME)));
        COMMON_COLUMNS.put(STATUS, new Column(COMMON_FIELDS.get(STATUS)));
//        COMMON_COLUMNS.put(APP_ID, new Column(COMMON_FIELDS.get(APP_ID)));
        COMMON_COLUMNS.put(PARENT_ID, new Column(COMMON_FIELDS.get(PARENT_ID)));
//        COMMON_COLUMNS.put(RECYCLE_FLAG, new Column(COMMON_FIELDS.get(RECYCLE_FLAG)));
//        COMMON_COLUMNS.put(RECYCLE_TIME, new Column(COMMON_FIELDS.get(RECYCLE_TIME)));
        COMMON_COLUMNS.put(RELATING, new Column(COMMON_FIELDS.get(RELATING)));
        COMMON_COLUMNS.put(BA_RELATING, new Column(COMMON_FIELDS.get(BA_RELATING)));
    }

    static {
        INSERT_EXCLUDED_FIELDS.addAll(Arrays.asList(CREATOR, CREATE_TIME, UPDATOR, UPDATE_TIME, STATUS, ID, WORKFLOW_NODE_ID));
        UPDATE_EXCLUDED_FIELDS.addAll(Arrays.asList(CREATOR, CREATE_TIME, UPDATOR, UPDATE_TIME, STATUS, WORKFLOW_NODE_ID));
        SELECT_EXCLUDED_FIELDS.addAll(Arrays.asList(CREATOR, CREATE_TIME, UPDATOR, UPDATE_TIME, STATUS, RECYCLE_FLAG, RECYCLE_TIME, ID, WORKFLOW_NODE_ID));
        UN_VALIDATE_EXCLUDED_FIELDS.addAll(Arrays.asList("issueStatus", "projectObjectTypeId", "iterationId"));

        for (FieldTypeEnums fieldTypeEnums: FieldTypeEnums.values()){
            if (fieldTypeEnums.isSupportedImport()) SUPPORTED_IMPORT_FIELD_TYPES.add(fieldTypeEnums.getFormFieldType());
        }
        SUPPORTED_IMPORT_FIELD_TYPES.addAll(Arrays.asList(CREATOR, CREATE_TIME, UPDATOR, UPDATE_TIME, WORKFLOW_NODE_ID));

        UN_RECORD_COLLABORATOR_FIELDS.addAll(Arrays.asList(CREATOR, UPDATOR));
        RECORD_COLLABORATOR_TYPES.addAll(Arrays.asList(FieldTypeEnums.USER.getFormFieldType(), FieldTypeEnums.WORK_HOUR.getFormFieldType(), FieldTypeEnums.DEPT.getFormFieldType()));
        NEED_DEAL_RELATING_TYPE.addAll(Arrays.asList(FieldTypeEnums.RELATING.getFormFieldType(), FieldTypeEnums.BA_RELATING.getFormFieldType(), FieldTypeEnums.SINGLE_RELATING.getFormFieldType()));
    }

    public static boolean isCommonField(String field){
        return COMMON_FIELDS.containsKey(field);
    }

    public static Map<String, FieldParam> getCommonFields(){
        return COMMON_FIELDS;
    }

    public static Map<String, Column> getCommonColumns(){
        return COMMON_COLUMNS;
    }

    public static Set<String> getInsertExcludedFields(){
        return INSERT_EXCLUDED_FIELDS;
    }

    public static Set<String> getUpdateExcludedFields(){
        return UPDATE_EXCLUDED_FIELDS;
    }
    public static Set<String> getSelectExcludedFields(){
        return SELECT_EXCLUDED_FIELDS;
    }

    public static boolean supportedImport(String fieldType){
        return SUPPORTED_IMPORT_FIELD_TYPES.contains(fieldType);
    }

}
