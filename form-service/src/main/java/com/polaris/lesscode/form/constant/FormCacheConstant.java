package com.polaris.lesscode.form.constant;

import com.polaris.lesscode.consts.CommonConsts;

/**
 * form import constant
 *
 * @author ethanliao
 * @date 2020/12/31
 * @since 1.0.0
 */
public interface FormCacheConstant {
    String FORM_IMPORT_CACHE_KEY_PREFIX = CommonConsts.LOCK_ROOT_KEY + "form:import:";

    String FORM_IMPORT_DATACOUNT_CACHE_KEY_PREFIX = FORM_IMPORT_CACHE_KEY_PREFIX + "datacount:";
    String FORM_IMPORT_CURRENTCOUNT_CACHE_KEY_PREFIX = FORM_IMPORT_CACHE_KEY_PREFIX + "currentcount:";
    String FORM_IMPORT_ADDCOUNT_CACHE_KEY_PREFIX = FORM_IMPORT_CACHE_KEY_PREFIX + "addcount:";
    String FORM_IMPORT_UPCOUNT_CACHE_KEY_PREFIX = FORM_IMPORT_CACHE_KEY_PREFIX + "upcount:";
    String FORM_IMPORT_SAMPLE_CACHE_KEY_PREFIX = FORM_IMPORT_CACHE_KEY_PREFIX + "sample:";


    String FORM_DATA_IMPORT_SAMPLES_CACHE_KEY = "form:import:samples:";
    String FORM_DATA_IMPORT_PROGRESS_CACHE_KEY = "form:import:progress:";
}
