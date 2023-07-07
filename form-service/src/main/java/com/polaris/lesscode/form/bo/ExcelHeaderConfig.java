package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.List;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-07-28 11:43 上午
 */
@Data
public class ExcelHeaderConfig {

    private String title;
    private String key;
    private List<ExcelHeaderConfig> childExcelConfig;
    private String format;
    private Integer type;
    private ExcelHeaderConfig parentConfig;
}
