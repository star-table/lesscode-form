/**
 * 
 */
package com.polaris.lesscode.form.service.tests.feign.mock;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.polaris.lesscode.app.internal.req.CreateAppReq;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.util.DateTimeUtils;
import com.polaris.lesscode.vo.Result;

///**
// * @author admin
// *
// */
//@RequestMapping("/test-appapi")
//public class AppApiImpl implements AppProvider {
//
//    @Override
//    public Result<List<AppResp>> getAppList(Long aLong, Long aLong1, Integer integer) {
//        return null;
//    }
//
//    @Override
//    public Result<AppResp> getAppInfo(Long aLong, Long aLong1) {
//        return null;
//    }
//
//    @Override
//    public Result<List<AppResp>> getAppInfoList(Long aLong, Collection<Long> collection) {
//        return null;
//    }
//
//    public Result<AppResp> getAppInfo(Long appId) {
//        AppResp resp = new AppResp();
//        Date now = new Date(DateTimeUtils.getCurrentDateTime().toEpochMilli());
//        resp.setCreateTime(now);
//        resp.setCreator(1001l);
//        resp.setIcon(null);
//        resp.setId(1298529572547035137l);
//        resp.setName("test1");
//        resp.setOrgId(1001l);
//        resp.setPkgId(1001l);
//        resp.setStatus(1);
//        resp.setType(1);
//        resp.setUpdateTime(now);
//        resp.setUpdator(1001l);
//        return Result.ok(resp);
//    }
//
//    @Override
//    public Result<TaskResp> startProcess(Long aLong, String s, Long aLong1) {
//        return null;
//    }
//
//    @Override
//    public Result<AppResp> createApp(CreateAppReq createAppReq) {
//        return null;
//    }
//
//}
