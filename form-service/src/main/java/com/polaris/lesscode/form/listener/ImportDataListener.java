package com.polaris.lesscode.form.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.form.bo.ImportProgress;
import com.polaris.lesscode.form.bo.ImportSamples;
import com.polaris.lesscode.form.constant.FormCacheConstant;
import com.polaris.lesscode.form.dto.Column;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.util.HeaderUtil;
import com.polaris.lesscode.form.req.AppValueAddReq;
import com.polaris.lesscode.form.req.AppValueUpdateReq;
import com.polaris.lesscode.form.service.AppFormService;
import com.polaris.lesscode.form.service.DataAddService;
import com.polaris.lesscode.form.service.DataUpdateService;
import com.polaris.lesscode.form.util.RedisUtil;
import com.polaris.lesscode.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ImportDataListener extends AnalysisEventListener<Map<Integer, Object>> {

    private DataAddService dataAddService;

    private DataUpdateService dataUpdateService;

    private AppFormService formService;

    private ImportSamples importSamples;

    private LinkedBlockingQueue<Map<String, Object>> queue;

    private Map<Integer, Column> columnMap;

    private Map<String, FieldParam> fieldParams;

    private Map<String, List<Long>> userKV;

    private Map<String, List<Long>> deptKV;

    private Map<String, List<Long>> roleKV;

    private RedisUtil redisUtil;

    private Integer batch = 50;

    private Integer total = 0;

    // 开始导入的下标，默认为1
    private int startIndex;

    // 当前游标
    private int currentIndex = 1;

    // 导入类型，1：仅新增，2：仅编辑，3：新增和编辑
    private int type;

    public ImportDataListener(
            ImportSamples importSamples,
            DataAddService dataAddService,
            DataUpdateService dataUpdateService,
            AppFormService formService,
            Map<String, FieldParam> fieldParams,
            Map<String, List<Long>> userKV,
            Map<String, List<Long>> deptKV,
            Map<String, List<Long>> roleKV,
            RedisUtil redisUtil,
            int index,
            int type
    ){
        this.dataAddService = dataAddService;
        this.dataUpdateService = dataUpdateService;
        this.formService = formService;
        this.fieldParams = fieldParams;
        this.userKV = userKV;
        this.deptKV = deptKV;
        this.roleKV = roleKV;
        this.importSamples = importSamples;
        this.queue = new LinkedBlockingQueue<>();
        this.columnMap = MapUtils.toMap(Column::getCol, importSamples.getColumns());
        this.redisUtil = redisUtil;
        this.startIndex = index;
        if (this.startIndex < 1){
            this.startIndex = 1;
        }
        this.type = type;
    }

    public void startImport(){
        refreshProgress(this, 0, 0, 0, 0);
        new Thread(new ImportAsyncRunnable(this)).start();
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        this.total = context.readSheetHolder().getApproximateTotalRowNumber() - 1;
    }

    @Override
    public void invoke(Map<Integer, Object> row, AnalysisContext context) {
        if (currentIndex++ < startIndex){ // 如果设置了开始导入的游标，改游标之前的数据不导入
            return;
        }
        Map<String, Object> data = new HashMap<>();
        for (Column column: importSamples.getColumns()){
            Object value = row.get(column.getCol());
            if (type != 1 && Objects.equals(column.getText(), "dataId")){
                if (StringUtils.isNumeric(String.valueOf(value))){
                    data.put("id", String.valueOf(value));
                }
            }else if (fieldParams.containsKey(column.getField()) && column.isImported()){
                FieldParam fieldParam = fieldParams.get(column.getField());
                FieldTypeEnums fieldType = FieldTypeEnums.formatByFieldType(fieldParam.getField().getType());
                if (fieldType == null){
                    continue;
                }
                if (TypeConverterFactory.isSupported(fieldType)){
                    if (importSamples.isCreated() && Objects.nonNull(value) && (fieldType == FieldTypeEnums.SELECT || fieldType == FieldTypeEnums.MULTISELECT)){
                        // 这里做options的添加
                        if (fieldType == FieldTypeEnums.MULTISELECT){
                            String[] optionValues = String.valueOf(value).split(",");
                            for (String v: optionValues){
                                HeaderUtil.appendSelectOptions(fieldParam, v);
                            }
                        }else{
                            HeaderUtil.appendSelectOptions(fieldParam, value);
                        }
                    }
                    // 类型转换
                    value = TypeConverterFactory.parse(fieldType, fieldParam, value);
                }else if (fieldType == FieldTypeEnums.USER){
                    value = toMemberIds(value == null ? null : value.toString(), userKV, true);
                }else if (fieldType == FieldTypeEnums.TREE_SELECT || fieldType == FieldTypeEnums.DEPT){
                    value = toMemberIds(value == null ? null : value.toString(), deptKV, false);
                }else if (fieldType == FieldTypeEnums.ROLE){
                    value = toMemberIds(value == null ? null : value.toString(), roleKV, false);
                }
                data.put(column.getField(), value);
            }
        }

        queue.add(data);
    }

    private List<String> toMemberIds(String value, Map<String, List<Long>> kv, boolean isMember){
        List<String> results = new ArrayList<>();
        if (StringUtils.isBlank(value)){
            return results;
        }
        if (! MapUtils.isEmpty(kv)){
            String[] names = value.split(",");
            for (String name: names){
                String[] infos = name.split("\\#");
                name = infos[0];
                List<Long> ids = kv.get(name);
                if (CollectionUtils.isNotEmpty(ids)){
                    if (infos.length > 1 && StringUtils.isNumeric(infos[1]) && ids.contains(Long.parseLong(infos[1]))){
                        results.add((isMember ? "U_" : "") + infos[1]);
                    }else{
                        results.add((isMember ? "U_" : "") + ids.get(0));
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Slf4j
    static class ImportAsyncRunnable implements Runnable{

        private ImportDataListener listener;

        private List<Map<String, Object>> datas;

        private int suc;

        private int insertSuc;

        private int updateSuc;

        private int fail;

        public ImportAsyncRunnable(ImportDataListener listener){
            this.listener = listener;
            this.datas = new ArrayList<>();
        }

        @Override
        public void run() {
            try {
                while (true){
                    Map<String, Object> data = listener.queue.poll(2, TimeUnit.SECONDS);
                    if (data == null){
                        break;
                    }
                    datas.add(data);
                    if (datas.size() >= listener.batch){
                        refreshData();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                refreshData();
                finishedProgress(listener, suc, fail, insertSuc, updateSuc);
            }
        }

        private void refreshData(){
            if (CollectionUtils.isNotEmpty(datas)){
                listener.formService.updateFormConfig(listener.importSamples.getOrgId(), listener.importSamples.getAppId(), new ArrayList<>(listener.fieldParams.values()));
                List<Map<String, Object>> insertDatas = new ArrayList<>();
                List<Map<String, Object>> updateDatas = new ArrayList<>();
                for (Map<String, Object> data: datas){
                    if (data.containsKey("id")){
                        updateDatas.add(data);
                    }else{
                        insertDatas.add(data);
                    }
                }
                if (CollectionUtils.isNotEmpty(insertDatas)){
                    // 新增
                    try{
                        if (listener.type == 2){
                            fail += insertDatas.size();
                        }else{
                            AppValueAddReq req = new AppValueAddReq();
                            req.setForm(insertDatas);
                            listener.dataAddService.add(listener.importSamples.getOrgId(), listener.importSamples.getUserId(), listener.importSamples.getAppId(), req, true, true, false);
                            suc += insertDatas.size();
                            insertSuc += insertDatas.size();
                        }
                    }catch(Exception e){
                        log.error("import insert err", e);
                        fail += insertDatas.size();
                    }
                }
                if (CollectionUtils.isNotEmpty(updateDatas)){
                    // 更新
                    try{
                        if (listener.type == 1){
                            fail += updateDatas.size();
                        }else{
                            AppValueUpdateReq req = new AppValueUpdateReq();
                            req.setForm(updateDatas);
                            listener.dataUpdateService.update(listener.importSamples.getOrgId(), listener.importSamples.getUserId(), listener.importSamples.getAppId(), req, true, true);
                            suc += updateDatas.size();
                            updateSuc += updateDatas.size();
                        }
                    }catch(Exception e){
                        log.error("import update err", e);
                        fail += updateDatas.size();
                    }
                }
                refreshProgress(listener, suc, fail, insertSuc, updateSuc);
                datas.clear();
            }
        }
    }

    private static void refreshProgress(ImportDataListener listener, int suc, int fail, int insertSuc, int updateSuc){
        suc = insertSuc + updateSuc;
        listener.redisUtil.set(FormCacheConstant.FORM_DATA_IMPORT_PROGRESS_CACHE_KEY + listener.importSamples.getToken(), JSON.toJSONString(new ImportProgress(listener.total, suc, fail, insertSuc, updateSuc)), 60 * 5);
    }

    private static void finishedProgress(ImportDataListener listener, int suc, int fail, int insertSuc, int updateSuc){
        suc = insertSuc + updateSuc;
        listener.redisUtil.set(FormCacheConstant.FORM_DATA_IMPORT_PROGRESS_CACHE_KEY + listener.importSamples.getToken(), JSON.toJSONString(new ImportProgress(suc + fail, suc, fail, insertSuc, updateSuc)), 60 * 5);
    }

}
