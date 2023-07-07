/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.polaris.lesscode.form.sensitive;

import org.apache.commons.lang3.StringUtils;

/**
 * 贴吧ID脱敏策略
 *
 * @author ethanliao
 * @date 2020/5/18
 * @since 1.0.0
 */
public class ChannelIDSensitiveFilter implements SensitiveFilter {
    @Override
    public Object desensitization(Object o) {
        if (o == null) {
            return null;
        }
        String id = String.valueOf(o);
        if (id.length() > 2) {
            return id.charAt(0) + StringUtils.repeat("*", id.length() - 2) + id.charAt(id.length() - 1);
        } else {
            return StringUtils.repeat("*", id.length());
        }
    }
}
