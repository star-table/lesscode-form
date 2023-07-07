package com.polaris.lesscode.form.handler;

import com.polaris.lesscode.form.bo.TableDatas;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.validator.ValidateError;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * 校验失败回调
 * @Author Nico
 * @Date 2021/5/26 11:43
 **/
public interface ValidateErrorCallBack {

    void handle(FieldParam fieldParam, Iterator<Map.Entry<String, Object>> it, Map<String, Object> data, ValidateError e);

}
