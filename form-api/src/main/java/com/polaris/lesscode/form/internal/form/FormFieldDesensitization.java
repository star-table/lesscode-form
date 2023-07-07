package com.polaris.lesscode.form.internal.form;

import lombok.Data;

/**
 * 脱敏配置
 *
 * @author Nico
 * @date 2021/3/10 13:54
 */
@Data
public class FormFieldDesensitization {

    /**
     * 是否启用
     **/
    private boolean enable;

    /**
     * 脱敏策略
     **/
    private String strategy;
}
