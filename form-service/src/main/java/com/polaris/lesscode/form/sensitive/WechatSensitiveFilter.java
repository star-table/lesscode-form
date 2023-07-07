package com.polaris.lesscode.form.sensitive;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author cindy
 * date: 2020/6/1
 * time: 11:30 AM
 * description: jeecg-boot-parent
 */
public class WechatSensitiveFilter implements SensitiveFilter {

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
