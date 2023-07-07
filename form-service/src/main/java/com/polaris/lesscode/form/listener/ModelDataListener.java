package com.polaris.lesscode.form.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.polaris.lesscode.form.constant.FormCacheConstant;
import com.polaris.lesscode.form.entity.AppForm;
import com.polaris.lesscode.form.enums.ImportType;
import com.polaris.lesscode.form.service.DataAddService;
import com.polaris.lesscode.form.util.RedisUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * model data listener
 *
 * @author ethanliao
 * @date 2020/12/31
 * @since 1.0.0
 */
public class ModelDataListener extends AnalysisEventListener<Map<Integer, Object>> {
    private static final int ADD_BATCH_COUNT = 30;
    List<Map<Integer, Object>> addVals = new ArrayList<>(ADD_BATCH_COUNT);

    private static final int UPDATE_BATCH_COUNT = 30;
    List<Map<Integer, Object>> upVals = new ArrayList<>(UPDATE_BATCH_COUNT);

    private Long orgId;
    private Long userId;
    private String fileName;
    private String type;
    DataAddService dataAddService;
    RedisUtil redisUtil;

    private long addCount = 0L;
    private long upCount = 0L;

    public ModelDataListener(Long orgId, Long userId, String fileName, String type,
                             RedisUtil redisUtil,
                             DataAddService dataAddService) {
        this.orgId = orgId;
        this.userId = userId;
        this.fileName = fileName;
        this.type = type;
        this.redisUtil = redisUtil;
        this.dataAddService = dataAddService;

    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        int dataCount = context.readSheetHolder().getApproximateTotalRowNumber() - 1;
        redisUtil.set(FormCacheConstant.FORM_IMPORT_DATACOUNT_CACHE_KEY_PREFIX + fileName, dataCount);
    }

    @Override
    public void invoke(Map<Integer, Object> val, AnalysisContext analysisContext) {
        ImportType importType = ImportType.parse(type);

        redisUtil.set(FormCacheConstant.FORM_IMPORT_CURRENTCOUNT_CACHE_KEY_PREFIX + fileName,
                analysisContext.readRowHolder().getRowIndex());

        switch (importType) {
            case INSERT:
                addVals.add(val);
                ++addCount;
                redisUtil.set(FormCacheConstant.FORM_IMPORT_ADDCOUNT_CACHE_KEY_PREFIX + fileName,
                        addCount);
                break;
            case UPDATE:
                upVals.add(val);
                ++upCount;
                redisUtil.set(FormCacheConstant.FORM_IMPORT_UPCOUNT_CACHE_KEY_PREFIX + fileName,
                        upCount);
                break;
            case UPSERT:
                if (val.containsKey("data_id")) {
                    upVals.add(val);
                    ++upCount;
                    redisUtil.set(FormCacheConstant.FORM_IMPORT_UPCOUNT_CACHE_KEY_PREFIX + fileName,
                            upCount);
                } else {
                    addVals.add(val);
                    ++addCount;
                    redisUtil.set(FormCacheConstant.FORM_IMPORT_ADDCOUNT_CACHE_KEY_PREFIX + fileName,
                            addCount);
                }
                break;
            case UNKNOWN:
                break;
            default:
                break;
        }

        if (addVals.size() >= ADD_BATCH_COUNT) {
//            appValueService.addBatchHandler(orgId, userId, appForm, addVals);
            addVals.clear();
        }

        if (upVals.size() >= UPDATE_BATCH_COUNT) {
//            appValueService.updateBatchHandler(orgId, userId, appForm, upVals);
            upVals.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        redisUtil.set(FormCacheConstant.FORM_IMPORT_CURRENTCOUNT_CACHE_KEY_PREFIX + fileName,
                analysisContext.readRowHolder().getRowIndex());

        if (!CollectionUtils.isEmpty(addVals)) {
//            appValueService.addBatchHandler(orgId, userId, appForm, addVals);
            addVals.clear();
        }

        if (!CollectionUtils.isEmpty(upVals)) {
//            appValueService.updateBatchHandler(orgId, userId, appForm, upVals);
            upVals.clear();
        }
    }
}
