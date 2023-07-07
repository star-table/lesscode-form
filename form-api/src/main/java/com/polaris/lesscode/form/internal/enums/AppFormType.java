package com.polaris.lesscode.form.internal.enums;

/**
 * @author: Liu.B.J
 * @data: 2020/10/24 11:50
 * @modified:
 */
public enum AppFormType {
    COMMON(1, "普通表单"),
    PROCESS(2, "流程表单");

    private final Integer type;
    private final String desc;

    AppFormType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static AppFormType formatOrNull(Integer type) {
        if (null == type) {
            return AppFormType.COMMON;
        }
        AppFormType[] enums = values();
        for (AppFormType _enu : enums) {
            if (_enu.getType().equals(type)) {
                return _enu;
            }
        }
        return AppFormType.COMMON;
    }

}
