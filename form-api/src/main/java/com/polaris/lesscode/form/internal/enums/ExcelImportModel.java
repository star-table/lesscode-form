package com.polaris.lesscode.form.internal.enums;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 2:21 下午
 */
public enum ExcelImportModel {
//仅新增数据
//仅更新数据
//更新和新增数据
    ADD(1, "仅新增数据"),
    UPDATE(2, "仅更新数据"),
    ADD_UPDATE(2, "更新和新增数据");

    private final Integer type;
    private final String desc;

    ExcelImportModel(Integer code, String desc) {
        this.type = code;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }


    public static ExcelImportModel formatOrNull(Integer type) {
        if (null == type) {
            return null;
        }
        ExcelImportModel[] enums = values();
        for (ExcelImportModel _enu : enums) {
            if (_enu.getType().equals(type)) {
                return _enu;
            }
        }

        return null;
    }

}
