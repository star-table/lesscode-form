package com.polaris.lesscode.form.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.bo.ImportSamples;
import com.polaris.lesscode.form.config.ValidatorContext;
import com.polaris.lesscode.form.dto.Column;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.resp.ImportValidateInfo;
import com.polaris.lesscode.form.service.DataValidateService;
import com.polaris.lesscode.form.validator.ValidateError;
import com.polaris.lesscode.util.MapUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class ImportValidateListener extends AnalysisEventListener<Map<Integer, Object>> {

    private DataValidateService dataValidateService;

    private ImportSamples importSamples;

    private Map<Integer, Column> columnMap;

    private BizForm bizForm;

    private Map<String, List<Long>> userKV;

    private Map<String, List<Long>> deptKV;

    private Map<String, List<Long>> roleKV;

    // 开始导入的下标，默认为1
    private int startIndex;

    // 当前游标
    private int currentIndex = 1;

    private List<ImportValidateInfo> infos;

    public ImportValidateListener(
            ImportSamples importSamples,
            DataValidateService dataValidateService,
            BizForm bizForm,
            Map<String, List<Long>> userKV,
            Map<String, List<Long>> deptKV,
            Map<String, List<Long>> roleKV,
            int index
    ){
        this.bizForm = bizForm;
        this.userKV = userKV;
        this.deptKV = deptKV;
        this.roleKV = roleKV;
        this.importSamples = importSamples;
        this.columnMap = MapUtils.toMap(Column::getCol, importSamples.getColumns());
        this.startIndex = index;
        if (this.startIndex < 1){
            this.startIndex = 1;
        }
        this.infos = new ArrayList<>();
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
    }

    @Override
    public void invoke(Map<Integer, Object> row, AnalysisContext context) {
        if (currentIndex++ < startIndex){ // 如果设置了开始导入的游标，改游标之前的数据不导入
            return;
        }

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> originalData = new HashMap<>();

        ImportValidateInfo info = new ImportValidateInfo();
        Map<String, String> errMsg = new HashMap<>();
        info.setData(originalData);
        info.setErrorMsg(errMsg);
        // 因为最开始已经++，所以这里-1才是真正的index
        info.setIndex(currentIndex - 1);
        for (Column column: importSamples.getColumns()){
            Object value = row.get(column.getCol());
            originalData.put(column.getField(), value);
            if (Objects.equals(column.getField(), "dataId")){
                data.put("id", String.valueOf(value));
            }else if (bizForm.getFieldParams().containsKey(column.getField()) && column.isImported()){
                FieldParam fieldParam = bizForm.getFieldParams().get(column.getField());
                FieldTypeEnums fieldType = FieldTypeEnums.formatByFieldType(fieldParam.getField().getType());
                if (fieldType == null){
                    continue;
                }
                if (TypeConverterFactory.isSupported(fieldType)){
                    // 直接走校验，不做类型转换
                    // 类型转换
                    // value = TypeConverterFactory.parse(fieldType, bizForm.getFieldParams().get(column.getField()), value);
                    try {
                        if (importSamples.isCreated() && (fieldType == FieldTypeEnums.SELECT || fieldType == FieldTypeEnums.MULTISELECT)){
                            // 不需要校验
                        }else{
                            value = TypeConverterFactory.parse(fieldType, bizForm.getFieldParams().get(column.getField()), value);
                            ValidatorContext.validate(fieldType, fieldParam, value);
                        }
                    } catch (ValidateError validateError) {
                        errMsg.put(fieldParam.getName(), validateError.getMessage());
                    }
                }else if (fieldType == FieldTypeEnums.USER){
                    String err = checkMember(value == null ? null : value.toString(), userKV);
                    if (StringUtils.isNotBlank(err)){
                        errMsg.put(fieldParam.getName(), err);
                    }
                }else if (fieldType == FieldTypeEnums.TREE_SELECT || fieldType == FieldTypeEnums.DEPT){
                    String err = checkMember(value == null ? null : value.toString(), deptKV);
                    if (StringUtils.isNotBlank(err)){
                        errMsg.put(fieldParam.getName(), err);
                    }
                }else if (fieldType == FieldTypeEnums.ROLE){
                    String err = checkMember(value == null ? null : value.toString(), roleKV);
                    if (StringUtils.isNotBlank(err)){
                        errMsg.put(fieldParam.getName(), err);
                    }
                }
                data.put(column.getField(), value);
            }
        }
        if (! MapUtils.isEmpty(errMsg)){
            infos.add(info);
        }
    }

    private String checkMember(String value, Map<String, List<Long>> kv){
        String errMsg = "";
        List<String> notExistNames = new ArrayList<>();
        List<String> repeatNames = new ArrayList<>();
        if (StringUtils.isBlank(value)){
            return null;
        }
        if (! MapUtils.isEmpty(kv)){
            String[] names = value.split(",");
            for (String name: names){
                String[] infos = name.split("\\#");
                name = infos[0];
                List<Long> ids = kv.get(name);
                if (CollectionUtils.isEmpty(ids)){
                    notExistNames.add(name);
                }else if (ids.size() > 1){
                    if (infos.length <= 1 || ! StringUtils.isNumeric(infos[1])){
                        repeatNames.add(name);
                    }
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(notExistNames)){
            builder.append(StringUtils.join(notExistNames.toArray(), ",")).append("不存在;");
        }
        if (CollectionUtils.isNotEmpty(repeatNames)){
            builder.append(StringUtils.join(repeatNames.toArray(), ",")).append("存在同名成员;");
        }
        return builder.toString();
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

}
