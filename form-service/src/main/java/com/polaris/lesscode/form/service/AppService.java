package com.polaris.lesscode.form.service;

import com.polaris.lesscode.app.internal.resp.AppResp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author roamer
 * @version v1.0
 * @date 2020-11-16 16:35
 */
public interface AppService {

    List<AppResp> getAppList(Long orgId);

    List<AppResp> getAppList(Long orgId, Collection<Long> appIds);

    Map<Long, AppResp> getAppMap(Long orgId);

    Map<Long, AppResp> getAppMap(Long orgId, Collection<Long> appIds);

    AppResp getApp(Long orgId, Long appId);
}
