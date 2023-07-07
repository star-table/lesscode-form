package com.polaris.lesscode.form.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.form.bo.ExcelReadObject;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取头
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class HeadDataListener extends AnalysisEventListener<LinkedHashMap<Integer, String>> {

    private ExcelReadObject excelReadObject;

    public HeadDataListener() {
    }

    public HeadDataListener(ExcelReadObject excelReadObject) {
        this.excelReadObject = excelReadObject;
    }

    @Override
    public void invoke(LinkedHashMap<Integer, String> integerStringMap, AnalysisContext analysisContext) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        excelReadObject.setRealHeader(headMap);
    }
}
