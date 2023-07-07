package com.polaris.lesscode.form.bo;

import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * 应用权限上下文
 *
 * @author Nico
 * @date 2021/3/8 17:46
 */
@Data
public class AppAuthorityContext {

    private AppAuthorityResp appAuthorityResp;

    public AppAuthorityContext(AppAuthorityResp appAuthorityResp) {
        this.appAuthorityResp = appAuthorityResp;
    }

    public AppAuthorityContext() {
    }

    public void writeDataFilter(Map<String, Object> data, Collection<String> excludedFields, Long tableId){
        if (MapUtils.isEmpty(data)){
            return;
        }
        data.entrySet().removeIf(current -> (excludedFields.contains(current.getKey()) || (current.getKey().startsWith("_") && !appAuthorityResp.hasFieldWriteAuth(tableId,current.getKey())))
        );
    }

    public void readDataFilter(Map<String, Object> data, Collection<String> includedFields, Long tableId){
        if (MapUtils.isEmpty(data)){
            return;
        }
        data.entrySet().removeIf(current -> ! includedFields.contains(current.getKey()) && ! appAuthorityResp.hasFieldReadAuth(tableId,current.getKey()));
    }

    public void readDataFilter(List<Map<String, Object>> datas, Collection<String> includedFields,  Long tableId){
        datas.forEach(data -> readDataFilter(data, includedFields,tableId));
    }
}
