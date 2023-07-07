package com.polaris.lesscode.form.internal.form;

import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import lombok.Data;

import java.util.List;

/**
 * 表单单选控件
 *
 * @author Nico
 * @date 2021/3/10 11:58
 */
@Data
public class FormFieldSelect {

   private List<FormFieldOptions> options;

}
