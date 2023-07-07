package com.polaris.lesscode.form.handler;

import com.polaris.lesscode.form.bo.TableDatas;
import com.polaris.lesscode.form.internal.sula.FieldParam;

import java.util.Map;

/**
 * 表单数据校验成功回调处理器
 *
 * @author: Liu.B.J
 * @date: 2021/1/26 18:28
 * @description:
 */
public interface ValidateFilterFun {

    void filter(Long dataId, String subformKey, Map<String, Object> data, FieldParam fieldParam);

}
