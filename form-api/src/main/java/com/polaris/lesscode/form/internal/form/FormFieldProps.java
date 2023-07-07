package com.polaris.lesscode.form.internal.form;

import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import lombok.Data;

/**
 * 表单字段
 *
 * @author Nico
 * @date 2021/3/10 11:58
 */
@Data
public class FormFieldProps {

    /**
     * 是否可写（是否让修改字段值）
     **/
    private boolean writ = true;

    /**
     * 是否可在表单设计器里编辑（通用字段该字段为false）
     **/
    private boolean edit = true;

    /**
     * 是否可筛选（如果该字段为false，则不会在筛选器中显示）
     **/
    private boolean filter = true;

    /**
     * 是否隐藏字段
     **/
    private boolean hidden = false;

    /**
     * 是否为系统字段
     **/
    private boolean isSystem;

    /**
     * 默认值
     **/
    private Object defaultValue;

    /**
     * 脱敏策略
     */
    private FormFieldDesensitization desensitization;

    /**
     * 单选
     **/
    private FormFieldSelect select;

    /**
     * 多选
     **/
    private FormFieldSelect multiselect;

}
