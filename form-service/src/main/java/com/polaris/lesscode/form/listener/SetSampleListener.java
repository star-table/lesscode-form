package com.polaris.lesscode.form.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.form.bo.ImportSamples;
import com.polaris.lesscode.form.constant.FormCacheConstant;
import com.polaris.lesscode.form.dto.Column;
import com.polaris.lesscode.form.dto.Match;
import com.polaris.lesscode.form.enums.ImportType;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.resp.ImportSampleResp;
import com.polaris.lesscode.form.util.RedisUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 导入samples监听器
 *
 * @Author Nico
 * @Date 2021/6/9 11:13
 **/
@Data
public class SetSampleListener extends AnalysisEventListener<Map<Integer, Object>> {
    private Map<Integer, CellData> headMap;
    private List<Map<Integer, Object>> samples;
    private Integer total = 0;

    private static final Map<CellDataTypeEnum, FieldTypeEnums> fieldTypes = new HashMap<>();
    static {
        fieldTypes.put(CellDataTypeEnum.STRING, FieldTypeEnums.SINGLE_TEXT);
        fieldTypes.put(CellDataTypeEnum.EMPTY, FieldTypeEnums.SINGLE_TEXT);
        fieldTypes.put(CellDataTypeEnum.ERROR, FieldTypeEnums.SINGLE_TEXT);
        fieldTypes.put(CellDataTypeEnum.IMAGE, FieldTypeEnums.SINGLE_TEXT);
        fieldTypes.put(CellDataTypeEnum.BOOLEAN, FieldTypeEnums.SINGLE_TEXT);
        fieldTypes.put(CellDataTypeEnum.NUMBER, FieldTypeEnums.NUMBER);
        fieldTypes.put(CellDataTypeEnum.DIRECT_STRING, FieldTypeEnums.SINGLE_TEXT);
    }

    public SetSampleListener(){
        this.samples = new ArrayList<>();
        this.headMap = new LinkedHashMap<>();
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        this.total = context.readSheetHolder().getApproximateTotalRowNumber() - 1;
        this.headMap = headMap;
    }

    /**
     * When analysis one row trigger invoke function.
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        if (context.readRowHolder().getRowIndex() <= 200) {
            samples.add(data);
        } else {
            throw new ExcelAnalysisStopException();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public ImportSamples parseSamples(List<FieldParam> fieldParams) {
        ImportSamples importSamples = new ImportSamples();

        Map<String, FieldParam> labelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(fieldParams)){
            fieldParams.forEach(fieldParam -> {
                labelMap.putIfAbsent(fieldParam.getLabel(), fieldParam);
            });
        }
        long timestamp = System.currentTimeMillis();
        List<Column> columns = new ArrayList<>();
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<Integer, String> fieldHeaders = new HashMap<>();
        for (Map.Entry<Integer, CellData> entry: headMap.entrySet()){
            Integer k = entry.getKey();
            CellData v = entry.getValue();
            Column column = new Column();
            column.setCol(k);
            column.setText(v.getStringValue());
            if (labelMap.containsKey(v.getStringValue())) {
                FieldParam fp = labelMap.get(v.getStringValue());
                column.setField(fp.getName());
                column.setType(fp.getField().getType());
            }else{
                column.setField("_field_" + (timestamp ++));
                column.setType(fieldTypes.get(v.getType()).getFormFieldType());
            }
            fieldHeaders.put(k, column.getField());
            columns.add(column);
        }
        if (CollectionUtils.isNotEmpty(this.samples)){
            for (Map<Integer, Object> sampleData: this.samples){
                Map<String, Object> data = new HashMap<>();
                for (Map.Entry<Integer, Object> sampleEntry: sampleData.entrySet()){
                    String key = fieldHeaders.get(sampleEntry.getKey());
                    if (StringUtils.isNotBlank(key)){
                        data.put(key, sampleEntry.getValue());
                    }
                }
                datas.add(data);
            }
        }
        importSamples.setColumns(columns);
        importSamples.setSamples(datas);
        return importSamples;
    }
}
