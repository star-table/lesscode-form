package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.form.internal.sula.*;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.gotable.internal.req.ReadSummeryTableIdRequest;
import com.polaris.lesscode.gotable.internal.resp.ReadSummeryTableIdResp;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用汇总表服务
 *
 * @author Nico
 * @date 2021/3/4 15:59
 */
@Service
public class AppSummaryService {

    @Autowired
    private AppService appService;

    @Autowired
    private GoTableService goTableService;

    public BizForm getBizForm(Long orgId, Long appId){
        return getBizForm(orgId, appId, null);
    }

    public BizForm getBizForm(Long orgId, Long appId, Long tableId, boolean isNeedRefColumn){
        AppResp appResp = appService.getApp(orgId, appId);
        if (appResp == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        if (Objects.equals(appResp.getType(), AppType.MIRROR.getCode())){
            appId = appResp.getMirrorAppId();
            appResp = appService.getApp(orgId, appId);
            if (appResp == null){
                throw new BusinessException(ResultCode.APP_NOT_EXIST);
            }
        }

        Map<String, FieldParam> fieldParams = new LinkedHashMap<>();
        Map<String, FieldParam> extendsFieldParams = new LinkedHashMap<>();
        TableSchemas tableResp;
        if (tableId != null && !tableId.equals(0L)) {
            tableResp = goTableService.readSchema(tableId, orgId, 0L, isNeedRefColumn);
        } else {
            Long summeryAppId = appId;
            if (appResp.getExtendsId() != null && appResp.getExtendsId() > 0){
                summeryAppId = appResp.getExtendsId();
            }
            tableResp = goTableService.readSchemaByAppId(summeryAppId, orgId, 0L);
        }
        if (tableResp == null) {
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }

        List<FieldParam> fields = new ArrayList<>();
        if (tableResp.getColumns() != null) {
            tableResp.getColumns().forEach(f -> {
                FieldParam fp  = JSON.toJavaObject(f,FieldParam.class);
                fields.add(fp);
                fieldParams.put(fp.getName(), fp);
            });
        }

        BizForm bizForm = new BizForm();
        bizForm.setOrgId(orgId);
        bizForm.setAppId(appId);
        bizForm.setTableId(tableId);
        bizForm.setAppName(appResp.getName());
        bizForm.setFormId(tableResp.getTableId());
        bizForm.setExtendsId(appResp.getExtendsId());
        bizForm.setFieldParams(fieldParams);
        bizForm.setExtendsFieldParams(extendsFieldParams);
        bizForm.setWorkflow(Objects.equals(appResp.getWorkflowFlag(), 1));
        bizForm.setAppType(appResp.getType());
        bizForm.setProjectId(appResp.getProjectId());

        if ((appResp.getExtendsId() != null && appResp.getExtendsId() > 0) || (tableId != null && !tableId.equals(0L))) {
            ReadSummeryTableIdResp summaryTableIdResp = goTableService.readSummeryTableId(new ReadSummeryTableIdRequest(orgId), orgId, 0L);
            if (summaryTableIdResp != null) {
                bizForm.setExtendsFormId(summaryTableIdResp.getTableId());
            }
        }

        FormJson config = new FormJson();
        config.setFields(fields);
        bizForm.setConfig(config);
        return bizForm;
    }

    /**
     * 获取业务表单对象
     *
     * @Author Nico
     * @Date 2021/3/4 16:47
     **/
    public BizForm getBizForm(Long orgId, Long appId, Long tableId){
        return getBizForm(orgId, appId, tableId, false);
    }

    public List<BizForm> getBizForms(Long orgId, Collection<Long> appIds){
        List<BizForm> bizForms = new ArrayList<>();

        List<AppResp> appResps = appService.getAppList(orgId, appIds);
        if (CollectionUtils.isNotEmpty(appResps)){
            Set<Long> extendsAppIds = appResps.stream().filter(a -> a.getExtendsId() != null && a.getExtendsId() > 0).map(AppResp::getExtendsId).collect(Collectors.toSet());
            Set<Long> beSelectedAppIds = appResps.stream().map(AppResp::getId).collect(Collectors.toSet());
            beSelectedAppIds.addAll(extendsAppIds);

//            List<FieldParam> baseFieldParams = appFormBaseService.getBaseFieldParams(orgId);
            for (AppResp appResp: appResps){
                TableSchemas tableResp = goTableService.readSchemaByAppId(appResp.getId(), orgId, 0L);

                Map<String, FieldParam> fieldParams = new LinkedHashMap<>();
                Map<String, FieldParam> extendsFieldParams = new LinkedHashMap<>();

                List<FieldParam> fields = new ArrayList<>(tableResp.getColumns().size());
                tableResp.getColumns().forEach(f -> {
                    FieldParam fp  = JSON.toJavaObject(f,FieldParam.class);
                    fields.add(fp);
                    fieldParams.put(fp.getName(), fp);
                });


                BizForm bizForm = new BizForm();
                bizForm.setOrgId(orgId);
                bizForm.setAppId(appResp.getId());
                bizForm.setFormId(tableResp.getTableId());
                bizForm.setExtendsId(appResp.getExtendsId());
                bizForm.setFieldParams(fieldParams);
                bizForm.setExtendsFieldParams(extendsFieldParams);
                bizForm.setWorkflow(Objects.equals(appResp.getWorkflowFlag(), 1));
                bizForm.setAppType(appResp.getType());
                bizForm.setProjectId(appResp.getProjectId());
                FormJson config = new FormJson();
                config.setFields(fields);

                bizForm.setConfig(config);
                bizForms.add(bizForm);
            }

        }
        return bizForms;
    }
}
