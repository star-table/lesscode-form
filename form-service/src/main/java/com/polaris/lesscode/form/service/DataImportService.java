package com.polaris.lesscode.form.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.bo.ImportProgress;
import com.polaris.lesscode.form.bo.ImportSamples;
import com.polaris.lesscode.form.constant.FormCacheConstant;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.listener.ImportDataListener;
import com.polaris.lesscode.form.listener.ImportValidateListener;
import com.polaris.lesscode.form.listener.SetSampleListener;
import com.polaris.lesscode.form.req.ImportDataReq;
import com.polaris.lesscode.form.req.ImportPreReq;
import com.polaris.lesscode.form.req.ImportRefreshReq;
import com.polaris.lesscode.form.req.ImportValidateReq;
import com.polaris.lesscode.form.resp.ImportPreResp;
import com.polaris.lesscode.form.resp.ImportValidateResp;
import com.polaris.lesscode.form.util.ExcelUtils;
import com.polaris.lesscode.form.util.RedisUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.GetMemberSimpleInfoReq;
import com.polaris.lesscode.uc.internal.resp.MemberSimpleInfo;
import com.polaris.lesscode.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * 数据导入
 *
 * @Author Nico
 * @Date 2021/5/8 10:44
 **/
@Slf4j
@Service
public class DataImportService {

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    private DataAddService dataAddService;

    @Autowired
    private AppFormService formService;

    @Autowired
    private DataUpdateService dataUpdateService;

    @Autowired
    private DataValidateService dataValidateService;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisUtil redisUtil;

    public ImportPreResp importPre(Long orgId, Long userId, ImportPreReq req){
        if (Objects.nonNull(req.getAppId()) && req.getAppId() > 0){
            AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, req.getAppId(), userId);
            if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_IMPORT.getCode())){
                throw new BusinessException(ResultCode.FORM_OP_NO_IMPORT);
            }
        }

        ImportPreResp importPreResp = new ImportPreResp();
        try {
            URL url = new URL(req.getExcel());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                SetSampleListener setSampleListener = new SetSampleListener();
                EasyExcel.read(conn.getInputStream(), setSampleListener).sheet(req.getSheetNo()).doRead();
                // 限制导入数据
                if (setSampleListener.getTotal() > 50000 || setSampleListener.getHeadMap().size() > 200){
                    throw new BusinessException(ResultCode.IMPORT_OUT_OF_LIMIT_ERROR);
                }

                List<FieldParam> fieldParams = null;
                if (Objects.nonNull(req.getAppId())){
                    BizForm bizForm = summaryService.getBizForm(orgId, req.getAppId());
                    if (bizForm == null){
                        throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
                    }
                    fieldParams = bizForm.getFieldList();
                }
                String token = RandomUtil.randomString(35);
                ImportSamples importSamples = setSampleListener.parseSamples(fieldParams);
                importSamples.setExcel(req.getExcel());
                importSamples.setAppId(req.getAppId());
                importSamples.setToken(token);
                importSamples.setOrgId(orgId);
                importSamples.setUserId(userId);
                importSamples.setSheetNo(req.getSheetNo());
                importSamples.setCreated(Objects.isNull(req.getAppId()) || Objects.equals(req.getAppId(), 0L));

                importPreResp.setColumns(importSamples.getColumns());
                importPreResp.setSamples(importSamples.getSamples());

                conn = (HttpURLConnection) url.openConnection();
                importPreResp.setSheets(ExcelUtils.sheets(conn.getInputStream()));
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, 30);

                importPreResp.setToken(token);
                importPreResp.setExpired(calendar.getTime());

                importSamples.setSamples(null);
                redisUtil.set(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + token, JSON.toJSONString(importSamples), 60 * 30 + 5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return importPreResp;
    }

    public void importData(Long orgId, Long userId, ImportDataReq req){
        String body = (String) redisUtil.get(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + req.getToken());
        if (StringUtils.isBlank(body)){
            throw new BusinessException(ResultCode.IMPORT_TOKEN_INVALID);
        }
        ImportSamples samples = JSON.parseObject(body, ImportSamples.class);
        if (samples.getAppId() == null){
            samples.setAppId(req.getAppId());
        }else if (! Objects.equals(samples.getAppId(), req.getAppId())){
            throw new BusinessException(ResultCode.IMPORT_TOKEN_NOT_MATCH_APP_ID);
        }
        if (samples.getAppId() == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        samples.setUserId(userId);
        samples.setOrgId(orgId);
        try {
            URL url = new URL(samples.getExcel());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                BizForm bizForm = summaryService.getBizForm(orgId, samples.getAppId());
                if (bizForm == null){
                    throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
                }
                GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
                getMemberSimpleInfoReq.setOrgId(orgId);
                getMemberSimpleInfoReq.setType(1);

                List<MemberSimpleInfo> users = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
                getMemberSimpleInfoReq.setType(2);
                List<MemberSimpleInfo> depts = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
                getMemberSimpleInfoReq.setType(3);
                List<MemberSimpleInfo> roles = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
                ImportDataListener importDataListener = new ImportDataListener(samples, dataAddService, dataUpdateService, formService, bizForm.getFieldParams(), memberSimpleInfosToMap(users), memberSimpleInfosToMap(depts), memberSimpleInfosToMap(roles), redisUtil, req.getIndex(), req.getType());
                EasyExcel.read(conn.getInputStream(), importDataListener).sheet(samples.getSheetNo()).doRead();
                importDataListener.startImport();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            redisUtil.del(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + req.getToken());
        }
    }

    private Map<String, List<Long>> memberSimpleInfosToMap(List<MemberSimpleInfo> members){
        Map<String, List<Long>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(members)){
            for (MemberSimpleInfo info: members){
                List<Long> ids = map.get(info.getName());
                if (ids == null){
                    ids = new ArrayList<>();
                    map.put(info.getName(), ids);
                }
                ids.add(info.getId());
            }
        }
        return map;
    }


    public ImportProgress importProgress(String token){
        String progress = (String) redisUtil.get(FormCacheConstant.FORM_DATA_IMPORT_PROGRESS_CACHE_KEY + token);
        if (StringUtils.isNotBlank(progress)){
            return JSON.parseObject(progress, ImportProgress.class);
        }
        return new ImportProgress(0, 0, 0, 0, 0);
    }

    public ImportPreResp importRefresh(Long orgId, Long userId, ImportRefreshReq req){
        String body = (String) redisUtil.get(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + req.getToken());
        if (StringUtils.isBlank(body)){
            throw new BusinessException(ResultCode.IMPORT_TOKEN_INVALID);
        }
        ImportSamples samples = JSON.parseObject(body, ImportSamples.class);
        samples.setColumns(req.getColumns());
        redisUtil.set(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + req.getToken(), JSON.toJSONString(samples), 60 * 30 + 5);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);

        ImportPreResp importPreResp = new ImportPreResp();
        importPreResp.setToken(req.getToken());
        importPreResp.setExpired(calendar.getTime());
        importPreResp.setColumns(req.getColumns());
        return importPreResp;
    }

    public ImportValidateResp importValidate(Long orgId, Long userId, ImportValidateReq req){
        String body = (String) redisUtil.get(FormCacheConstant.FORM_DATA_IMPORT_SAMPLES_CACHE_KEY + req.getToken());
        if (StringUtils.isBlank(body)){
            throw new BusinessException(ResultCode.IMPORT_TOKEN_INVALID);
        }
        ImportSamples samples = JSON.parseObject(body, ImportSamples.class);
        if (samples.getAppId() == null){
            samples.setAppId(req.getAppId());
        }else if (! Objects.equals(samples.getAppId(), req.getAppId())){
            throw new BusinessException(ResultCode.IMPORT_TOKEN_NOT_MATCH_APP_ID);
        }
        samples.setUserId(userId);
        samples.setOrgId(orgId);

        ImportValidateResp resp = new ImportValidateResp();
        try {
            URL url = new URL(samples.getExcel());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new BusinessException(ResultCode.IMPORT_EXCEL_INVALID);
            }
            BizForm bizForm = summaryService.getBizForm(orgId, samples.getAppId());
            if (bizForm == null){
                throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
            }
            GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
            getMemberSimpleInfoReq.setOrgId(orgId);
            getMemberSimpleInfoReq.setType(1);
            List<MemberSimpleInfo> users = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
            getMemberSimpleInfoReq.setType(2);
            List<MemberSimpleInfo> depts = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
            getMemberSimpleInfoReq.setType(3);
            List<MemberSimpleInfo> roles = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();

            ImportValidateListener importValidateListener = new ImportValidateListener(samples, dataValidateService, bizForm, memberSimpleInfosToMap(users), memberSimpleInfosToMap(depts), memberSimpleInfosToMap(roles), req.getIndex());
            EasyExcel.read(conn.getInputStream(), importValidateListener).sheet(samples.getSheetNo()).doRead();
            resp.setColumns(samples.getColumns());
            resp.setInfos(importValidateListener.getInfos());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

}
