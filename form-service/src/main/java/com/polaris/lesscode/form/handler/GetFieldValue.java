package com.polaris.lesscode.form.handler;

import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;

/**
 * @author: Liu.B.J
 * @date: 2021/2/1 16:32
 * @description:
 */

public interface GetFieldValue {

    Long getIncr(String tableName, String fieldKey, Long dataId, Long initialNum, DataCenterProvider dataCenterProvider);

}
