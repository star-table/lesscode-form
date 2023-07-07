package com.polaris.lesscode.form.internal.enums;

import com.polaris.lesscode.enums.StorageFieldType;
import org.apache.commons.lang3.StringUtils;


/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 10:08 上午
 */
public enum FieldTypeEnums {
    SINGLE_TEXT(0, "单行文本", StorageFieldType.STRING, "input", "input", true),
    MUL_TEXT(1, "多行文本", StorageFieldType.TEXT, "textarea", "textarea", true),
    DATE(2, "日期", StorageFieldType.DATE, "datepicker", "datepicker", true),
    EMAIL(3, "邮箱", StorageFieldType.STRING, "email", "email", true),
    PHONE(4, "手机", StorageFieldType.STRING, "mobile", "mobile", true),
    NUMBER(5, "数字", StorageFieldType.DOUBLE, "inputnumber", "inputnumber", true),
    //SINGLE_CHOICE(6, "单项选择", StorageFieldType.STRING, "radiogroup","radiogroup"),
    //MUL_CHOICE(7, "多项选择", StorageFieldType.ARRAY, "checkboxgroup","checkboxgroup"),
    //DURING_DATE(8, "起止时间", StorageFieldType.DATE, "rangepicker","rangepicker"),
    //ADDRESS(9, "地址", StorageFieldType.STRING, "address","address"),
    USER(11, "成员", StorageFieldType.STRING, "member", "member", true),
    DEPT(12, "部门", StorageFieldType.LONG, "dept", "dept", true),
    TREE_SELECT(13, "部门下拉树", StorageFieldType.STRING, "treeSelect", "treeSelect", true),
    SELECT(15, "下拉框", StorageFieldType.STRING, "select", "select", true),
    SUBFORM(16, "子表单", StorageFieldType.ARRAY, "subform", "subform", false),
    //CARD(17, "卡片", StorageFieldType.ARRAY, "card","card"),
    STATUS(18, "启用状态", StorageFieldType.INT, "status", "status", true),
    //TIME(19, "时间", StorageFieldType.STRING,"timepicker","timepicker"),
    AMOUNT(20, "金额", StorageFieldType.STRING, "amount", "amount", true),
    DOCUMENT(21, "附件", StorageFieldType.CUSTOM, "document", "document", false),
    //FORMAT(22, "公式", StorageFieldType.STRING, "format", "format"),
    RELATION_TABLE(23, "关联", StorageFieldType.STRING, "relateTable", "relateTable", false),
    MULTISELECT(24, "多选下拉框", StorageFieldType.STRING, "multiselect", "multiselect", true),
    REGION(25, "省市区", StorageFieldType.STRING, "region", "region", false),
    LINK(26, "链接", StorageFieldType.STRING, "link", "link", true),
    RICH_TEXT(27, "富文本", StorageFieldType.STRING, "richtext", "richtext", true),
    IMAGE(28, "图片", StorageFieldType.STRING, "image", "image", false),
    AUTO_NUMBER(29, "自增", StorageFieldType.LONG, "autonum", "autonum", true),
    QUOTE_TABLE(30, "引用", StorageFieldType.STRING, "quoteTable", "quoteTable", false),
    OPERATE(31, "操作", StorageFieldType.STRING, "operate", "operate", false),
    RECYCLE_FLAG(32, "回收", StorageFieldType.STRING, "recycleFlag", "recycleFlag", false),
    RELATION_TABLE_PRO(33, "高级关联", StorageFieldType.STRING, "relateTablePro", "relateTablePro", false),
    //LINKDATA(20,"关联数据",StorageFieldType.DATALINK ,"datalink","datalink"),
    //IP(33, "地区ip", StorageFieldType.STRING, "ip", "ip", true),
    LOGTABLE(34, "日志表格", StorageFieldType.STRING, "logTable", "logTable", false),
    RANGE_NUMBER(35, "数字区间", StorageFieldType.DOUBLE, "rangeNumber", "rangeNumber", false),
    // 自定义字段，给前端做扩展
    CUSTOM_FIELD(36, "自定义字段", StorageFieldType.TEXT, "customField", "customField", false),
    IDENTITY_CARD(37, "身份证", StorageFieldType.TEXT, "identityCard", "identityCard", true),
    ROLE(38, "角色", StorageFieldType.STRING, "role", "role", true),
    GPS(39, "定位坐标", StorageFieldType.ARRAY, "gps", "gps", false),
    GROUP_SELECT(40, "分组下拉框", StorageFieldType.STRING, "groupSelect", "groupSelect", true),

    RELATING(41, "关联", StorageFieldType.CUSTOM, "relating", "relating", true),
    BA_RELATING(42, "前后置", StorageFieldType.CUSTOM, "baRelating", "baRelating", true),
    WORK_HOUR(43, "工时", StorageFieldType.CUSTOM, "workHour", "workHour", true),
//    ATTACHMENT(44, "附件", StorageFieldType.STRING, "groupSelect", "groupSelect", true),
    SINGLE_RELATING(45, "单向关联", StorageFieldType.CUSTOM, "singleRelating", "singleRelating", true),
    CONDITION_REF(46, "条件引用", StorageFieldType.CUSTOM,"conditionRef", "conditionRef", false),
    REFERENCE(48, "引用", StorageFieldType.CUSTOM, "reference", "reference", true)
    ;

    private Integer code;
    private String desc;
    private StorageFieldType type;
    private String formFieldType;
    private String dataType;
    private boolean supportedImport;

    FieldTypeEnums(Integer code, String desc, StorageFieldType type, String formFieldType, String dataType, boolean supportedImport) {
        this.code = code;
        this.desc = desc;
        this.type = type;
        this.formFieldType = formFieldType;
        this.dataType = dataType;
        this.supportedImport = supportedImport;
    }


    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public StorageFieldType getType() {
        return type;
    }

    public String getFormFieldType() {
        return formFieldType;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isSupportedImport() {
        return supportedImport;
    }

    public static FieldTypeEnums formatOrNull(Integer code) {
        if (null == code) {
            return null;
        }
        FieldTypeEnums[] enums = values();
        for (FieldTypeEnums _enu : enums) {
            if (_enu.getCode().equals(code)) {
                return _enu;
            }
        }

        return null;
    }

    public static FieldTypeEnums formatOrNull(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return null;
        }
        FieldTypeEnums[] enums = values();
        for (FieldTypeEnums _enu : enums) {
            if (_enu.getDataType().equals(dataType)) {
                return _enu;
            }
        }

        return null;
    }

    public static FieldTypeEnums formatByFieldType(String formFieldType) {
        if (StringUtils.isBlank(formFieldType)) {
            return null;
        }
        FieldTypeEnums[] enums = values();
        for (FieldTypeEnums _enu : enums) {
            if (_enu.getFormFieldType().equals(formFieldType)) {
                return _enu;
            }
        }

        return null;
    }


    public static FieldTypeEnums format(Integer code) {
        FieldTypeEnums se = formatOrNull(code);
        return null == se ? SINGLE_TEXT : se;
    }
}


