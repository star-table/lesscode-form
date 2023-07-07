package com.polaris.lesscode.form.internal.form;

import lombok.Data;

import java.util.Map;

/**
 * 表单字段
 *
 * @author Nico
 * @date 2021/3/10 11:58
 */
@Data
public class FormField {

    private String key;

    private String name;

    private String remark;

    private String type;

    private FormFieldRules rules;

    private FormFieldProps props;

    private Map<String, Object> ext;
}
