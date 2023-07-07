package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-07-27 2:11 下午
 */
@Data
public class ExcelWriteObject {

    private String filePath;

    private String sheetName;

    private List<ExcelHeaderConfig> headerConfigs;

    private List<LinkedHashMap<String, Object>> data;
}
