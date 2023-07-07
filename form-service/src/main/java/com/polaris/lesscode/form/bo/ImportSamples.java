package com.polaris.lesscode.form.bo;

import com.alibaba.excel.read.metadata.ReadSheet;
import com.polaris.lesscode.form.dto.Column;
import lombok.Data;
import org.apache.poi.ss.formula.functions.Columns;

import java.util.List;
import java.util.Map;

@Data
public class ImportSamples {

    private List<Map<String, Object>> samples;

    private List<Column> columns;

    private List<ReadSheet> sheets;

    private String excel;

    private Long appId;

    private Long userId;

    private Long orgId;

    private String token;

    private Integer sheetNo;

    private boolean isCreated;
}
