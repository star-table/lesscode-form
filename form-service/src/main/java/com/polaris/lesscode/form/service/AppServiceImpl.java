package com.polaris.lesscode.form.service;

import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author roamer
 * @version v1.0
 * @date 2020-11-16 16:38
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppProvider appProvider;

    @Override
    public List<AppResp> getAppList(Long orgId) {
        Result<List<AppResp>> result = appProvider.getAppList(orgId, null, null);
        return result.getData();
    }

    @Override
    public List<AppResp> getAppList(Long orgId, Collection<Long> appIds) {
        if (Objects.isNull(appIds) || appIds.isEmpty()) {
            return Collections.emptyList();
        }
        Result<List<AppResp>> result = appProvider.getAppInfoList(orgId, appIds);
        return result.getData();
    }

    @Override
    public Map<Long, AppResp> getAppMap(Long orgId) {
        return getAppList(orgId).stream().collect(Collectors.toMap(AppResp::getId, v -> v));
    }

    @Override
    public Map<Long, AppResp> getAppMap(Long orgId, Collection<Long> appIds) {
        if (Objects.isNull(appIds) || appIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return getAppList(orgId, appIds).stream().collect(Collectors.toMap(AppResp::getId, v -> v));
    }

    @Override
    public AppResp getApp(Long orgId, Long appId) {
        return appProvider.getAppInfo(orgId, appId).getData();
    }
}
