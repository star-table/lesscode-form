package com.polaris.lesscode.form.util;

import com.polaris.lesscode.permission.internal.api.AppPackagePermissionApi;
import com.polaris.lesscode.permission.internal.enums.FormFieldAuthCode;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.feign.PermissionProvider;
import com.polaris.lesscode.permission.internal.model.UserPermissionVO;
import com.polaris.lesscode.permission.internal.model.req.ModifyAppPackagePermissionReq;
import com.polaris.lesscode.permission.internal.model.req.PermissionMembersItemReq;
import com.polaris.lesscode.permission.internal.model.resp.FromPerOptAuthVO;
import com.polaris.lesscode.permission.internal.model.resp.SimpleAppPermissionResp;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author: Liu.B.J
 * @data: 2020/9/24 13:06
 * @modified:
 */
@Component
public class PermissionUtil {

    @Autowired
    private AppPackagePermissionApi appPkgPermissionProvider;

    @Autowired
    private PermissionProvider permissionProvider;

    @Autowired
    private AppPermissionProvider appPermissionProvider;

    /**
     * 执行创建/修改应用包权限
     *
     * @param appPkgId
     * @param orgId
     * @param userId
     * @param scope    权限范围
     * @param members  权限成员
     */
    public Result<Boolean> saveOrUpdateAppPkgPer(Long appPkgId, Long orgId, Long userId, Integer scope, List<PermissionMembersItemReq> members) {
        ModifyAppPackagePermissionReq req = new ModifyAppPackagePermissionReq();
        req.setAppPackageId(appPkgId);
        req.setOrgId(orgId);
        req.setUserId(userId);
        req.setScope(scope);
        if (null == members) {
            req.setMembers(new ArrayList<PermissionMembersItemReq>());
        } else {
            req.setMembers(members);
        }
        return appPkgPermissionProvider.saveOrUpdateAppPackagePermission(req);
    }

    /**
     * 获取应用包权限 简要信息
     *
     * @param orgId
     * @param appPkgId
     * @return
     */
    public Result<SimpleAppPermissionResp> getSimpleAppPackagePermission(Long orgId, Long appPkgId) {
        return appPkgPermissionProvider.getSimpleAppPackagePermission(orgId, appPkgId);
    }

    /**
     * 获取应用权限组-操作权限
     *
     * @param orgId
     * @param appId
     * @param userId
     * @return
     */
    public Result<FromPerOptAuthVO> getOptAuth(Long orgId, Long appId, Long userId) {
        return appPermissionProvider.getOptAuth(orgId, appId, userId);
    }


    /**
     * 获取成员权限
     *
     * @param orgId
     * @param userId
     * @return
     */
    public Result<UserPermissionVO> getUserPermission(Long orgId, Long userId) {
        return permissionProvider.getUserPermission(orgId, userId);
    }

    /**
     * 获取成员权限
     *
     * @param orgId
     * @param userId
     * @return
     */
    public Map<String, Map<String, FormFieldAuthCode>> getFieldAuth(Long orgId, Long appId, Long userId) {
        Map<String, Map<String, Integer>> data = appPermissionProvider.getFieldAuth(orgId, appId, userId).getData();
        Map<String, Map<String, FormFieldAuthCode>> formFieldAuthCodeMap = new HashMap<>(data.size());
        data.forEach((k, v) -> {
            Map<String, FormFieldAuthCode> fa = new HashMap<>(v.size());
            v.forEach((k1, v1) -> {
                if (Objects.nonNull(v1)) {
                    fa.put(k1, FormFieldAuthCode.forValue(v1));
                }
            });
            formFieldAuthCodeMap.put(k, fa);

        });
        return formFieldAuthCodeMap;
    }
}
