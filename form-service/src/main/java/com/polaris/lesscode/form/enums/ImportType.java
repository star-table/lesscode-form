package com.polaris.lesscode.form.enums;

public enum ImportType {
    INSERT("insert", "仅新增数据"),
    UPDATE("update", "仅更新数据"),
    UPSERT("upsert", "更新和新增数据"),
    UNKNOWN("unknown", ""),
    ;
    private String type;
    private String desc;

    ImportType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ImportType parse(String type) {
        for (ImportType Enum : ImportType.values()) {
            if (Enum.getType().equalsIgnoreCase(type)) {
                return Enum;
            }
        }
        return UNKNOWN;
    }

    public String getType() {
        return type;
    }
}
