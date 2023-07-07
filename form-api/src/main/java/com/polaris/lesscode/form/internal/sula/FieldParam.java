package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.dc.internal.dsl.Agg;
import com.polaris.lesscode.dc.internal.dsl.ColumnInfo;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import com.polaris.lesscode.form.internal.util.HeaderUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Liu.B.J
 */
@Data
public class FieldParam {

	/**
	 * 0名称--json数据 key
	 */
    private String name;

    /**
     *1标签，页面显示字段
     */
    private String label;
    private String enLabel;
    private String aliasLabel;

    /**
     *2 控件说明-补充
     */
    private Field field;

    /**
     * 4校验规则
     */
    private List<RuleParam> rules;

    /**
     * 5
     */
    private String valuePropName;

    /**
     * 6
     */
    private Container container;

    /**
     * 7子表单集合
     */
    private List<FieldParam> fields;

    /**
     * 8初始值
     */
    private Object initialValue;

    /**
     * 9 初始可见
     */
    private Boolean initialVisible = true;

    /**
     * 10 初始可用
     */
    private Boolean initialDisabled = false;

    private Boolean collect;

    private Boolean colon;

    private Boolean noStyle;

    private Boolean initialAdd = false;

    private Boolean initialDelete = false;

    /**
     * 是否可写（是否让修改字段值）
     **/
    private Boolean writable = true;

    /**
     * 是否可在表单设计器里编辑（通用字段该字段为false）
     **/
    private Boolean editable = true;

    /**
     * 是否可筛选（如果该字段为false，则不会在筛选器中显示）
     **/
    private Boolean filterable = true;

    /**
     * 是否唯一
     **/
    private Boolean unique = false;

    /**
     * 唯一预处理函数，"urlDomainParse": url域名解析，保留一位
     **/
    private String uniquePreHandler;

    /**
     * 是否是系统字段
     **/
    private Boolean isSys = false;

    /**
     * 是否是组织字段
     */
    private Boolean isOrg = false;

    /**
     * 14用户布局
     */
    private Map<String, Object> itemLayout;

    /**
     * 15初始值
     */
    private List<InitialSourceParam> initialSource;

    /**
     * 脱敏策略
     */
    private String sensitiveStrategy;

    /**
     * 是否脱敏
     */
    private Integer sensitiveFlag = YesOrNoEnum.NO.getCode();

   

    public Boolean isLinkField(){
        Field f = this.getField();
        if(f != null && (
                FieldTypeEnums.DOCUMENT.getFormFieldType().equals(f.getType())
                || FieldTypeEnums.IMAGE.getFormFieldType().equals(f.getType())
                || FieldTypeEnums.LINK.getFormFieldType().equals(f.getType()))
        ){
            return true;
        }
        return false;
    }

    public Boolean isSubForm(){
        Field f = this.getField();
        if(f != null && FieldTypeEnums.SUBFORM.getFormFieldType().equals(f.getType())){
            return true;
        }
        return false;
    }

    public FieldParam(Field field, String name, String label, Boolean isSys){
        this.field = field;
        this.name = name;
        this.label = label;
        this.isSys = isSys;
    }

    public FieldParam(){
    }

    /**
     * 构建FieldParam
     *
     * @Author Nico
     * @Date 2021/2/25 11:27
     **/
    public static FieldParam buildSystem(String name, String label, FieldTypeEnums fieldType){
        FieldParam fieldParam = new FieldParam();
        fieldParam.setName(name);
        fieldParam.setLabel(label);
        fieldParam.setIsSys(true);
        Field field = new Field();
        field.setType(fieldType.getFormFieldType());
        field.setDataType(fieldType.getType());
        fieldParam.setField(field);
        return fieldParam;
    }

    public static Column buildColumn(FieldParam p){
        Column c = new Column();
        c.setKey(p.getName());
        c.setTitle(p.getLabel());
        c.setEnTitle(p.getEnLabel());
        c.setAliasTitle(p.getAliasLabel());
        c.setField(p.getField());
        c.setUnique(p.getUnique());
        c.setRules(p.getRules());
        c.setIsSys(p.getIsSys());
        c.setEditable(p.getEditable());
        c.setWritable(p.getWritable());
        if (!CollectionUtils.isEmpty(p.getFields())) {
            List<Children> children = p.getFields().stream().map(p2 -> {
                Children cr = new Children();
                cr.setKey(p2.getName());
                cr.setTitle(p2.getLabel());
                cr.setField(p2.getField());
                cr.setUnique(p2.getUnique());
                cr.setRules(p2.getRules());
                return cr;
            }).collect(Collectors.toList());
            c.setChildren(children);
        }
        return c;
    }

    public boolean checkIsArrayColumn() {
        FieldTypeEnums fieldType = FieldTypeEnums.formatByFieldType(getField().getType());
        if (fieldType == null) {
            return false;
        }

        if (Objects.equals(getName(), "creator") || Objects.equals(getName(), "updator")) {
           return false;
        }

        return Objects.equals(fieldType, FieldTypeEnums.USER) || Objects.equals(fieldType, FieldTypeEnums.DEPT) || Objects.equals(fieldType, FieldTypeEnums.MULTISELECT);
    }

    public static void setColumnInfo(FieldParam fieldParam, ColumnInfo columnInfo) {
        if (fieldParam != null) {
            columnInfo.setType(getColumnType(fieldParam));
            columnInfo.setOptions(HeaderUtil.parseSelectOptions(fieldParam));
        }
    }

    public static boolean checkIsArrayColumn(FieldParam field) {
        if (field == null) {
            return false;
        }

        return field.checkIsArrayColumn();
    }

    public static boolean checkIsFieldType(FieldParam fieldParam, FieldTypeEnums type) {
        if (fieldParam == null) {
            return false;
        }

        FieldTypeEnums fieldType = FieldTypeEnums.formatByFieldType(fieldParam.getField().getType());
        if (fieldType == null) {
            return false;
        }

        return Objects.equals(fieldType, type);
    }

    public static String getColumnType(FieldParam fieldParam) {
        if (fieldParam == null || fieldParam.getField() == null) {
            return ColumnInfo.STRING;
        }
        String fieldType = fieldParam.getField().getType();
        if (FieldTypeEnums.DEPT.getFormFieldType().equals(fieldType) || FieldTypeEnums.USER.getFormFieldType().equals(fieldType)) {
            return ColumnInfo.MEMBER;
        } else if (FieldTypeEnums.SELECT.getFormFieldType().equals(fieldType) || FieldTypeEnums.GROUP_SELECT.getFormFieldType().equals(fieldType)) {
            return ColumnInfo.SELECT;
        } else if(FieldTypeEnums.MULTISELECT.getFormFieldType().equals(fieldType)) {
            return ColumnInfo.MULTI_SELECT;
        }

        return ColumnInfo.STRING;
    }
}
