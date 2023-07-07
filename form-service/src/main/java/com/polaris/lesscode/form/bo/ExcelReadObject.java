package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-07-27 3:19 下午
 */
@Data
public class ExcelReadObject {

    private List<LinkedHashMap<String, Object>> data;

    private Map<Integer, String> header;

    private Map<Integer, String> realHeader;

    private List<ExcelHeaderConfig> headerConfigs;
}
