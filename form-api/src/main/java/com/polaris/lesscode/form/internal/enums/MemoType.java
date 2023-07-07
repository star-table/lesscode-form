package com.polaris.lesscode.form.internal.enums;

/**
 * @author: Liu.B.J
 * @data: 2020/10/24 11:50
 * @modified:
 */
public enum MemoType {
    NONE(0, "无"),
    STAR(1, "星标");

    private final Integer type;
    private final String desc;

    MemoType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static MemoType formatOrNull(Integer type) {
        if (null == type) {
            return MemoType.NONE;
        }
        MemoType[] enums = values();
        for (MemoType _enu : enums) {
            if (_enu.getType().equals(type)) {
                return _enu;
            }
        }

        return MemoType.NONE;
    }

}
