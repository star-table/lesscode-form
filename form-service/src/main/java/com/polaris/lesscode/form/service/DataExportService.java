package com.polaris.lesscode.form.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.*;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.util.HeaderUtil;
import com.polaris.lesscode.form.req.AppValueListReq;
import com.polaris.lesscode.form.req.ExportDataReq;
import com.polaris.lesscode.form.req.ExportTemplateReq;
import com.polaris.lesscode.form.util.*;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.GetRepeatMemberReq;
import com.polaris.lesscode.uc.internal.resp.RepeatMemberInfo;
import com.polaris.lesscode.uc.internal.resp.RepeatMemberInfoResp;
import com.polaris.lesscode.util.MapUtils;
import com.polaris.lesscode.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据单元功能服务，提供简单的功能
 *
 * @author Nico
 * @date 2021-02-21
 */
@Slf4j
@Service
public class DataExportService {

    @Autowired
    private AppSummaryService summaryService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Autowired
    private DataFilterService dataFilterService;

    @Autowired
    private AppProvider appProvider;

    @Autowired
    private UserCenterApi userCenterApi;

    @Value("${excel.rootPath}")
    private String rootPath;

    @Value("${excel.localDomain}")
    private String localDomain;

    public String export(Long orgId, Long userId, OutputStream outputStream, ExportDataReq req) {
        AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, req.getAppId(), userId);
        if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_EXPORT.getCode())){
            throw new BusinessException(ResultCode.FORM_OP_NO_EXPORT);
        }

        AppResp app = appProvider.getAppInfo(orgId, req.getAppId()).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        List<FieldParam> fieldParams = exportFields(orgId, app.getId());
        Map<String, FieldParam> fieldParamMap = MapUtils.toMap(FieldParam::getName, fieldParams);
        List<String> exportFields = fieldParams.stream().map(FieldParam::getName).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(req.getFields())){
            exportFields.removeIf(f -> ! req.getFields().contains(f));
        }
        if (CollectionUtils.isEmpty(exportFields)){
            throw new BusinessException(ResultCode.EXPORT_FIELDS_IS_EMPTY);
        }

        exportFields.add(0, "id");
        List<List<String>> headers = new ArrayList<>();
        if (Objects.equals(req.getEnableId(), true)){
            headers.add(Collections.singletonList("dataId"));
        }
        for (String field: exportFields){
            if (fieldParamMap.containsKey(field)){
                String label = fieldParamMap.get(field).getLabel();
                headers.add(Collections.singletonList(label));
            }
        }
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.write(outputStream).head(headers).sheet(app.getName().replaceAll("/", "_"));
        long page = 1;
        long size = 1000;
        while (true){
            AppValueListReq filterReq = new AppValueListReq();
            filterReq.setColumns(exportFields);
            filterReq.setOrders(req.getOrders());
            filterReq.setCondition(req.getCondition());
            filterReq.setPage((int)page);
            filterReq.setSize((int)size);
            Page<Map<String, Object>> pages = dataFilterService.filter(orgId, userId, req.getAppId(), filterReq);
            if (CollectionUtils.isEmpty(pages.getList())){
                excelWriterSheetBuilder.doWrite(new ArrayList<>());
                break;
            }
            page ++;
            List<List<Object>> writeDatas = new ArrayList<>();
            for (Map<String, Object> data: pages.getList()) {
                List<Object> writeData = new ArrayList<>();
                if (Objects.equals(req.getEnableId(), true)){
                    writeData.add(String.valueOf(data.get("id")));
                }
                for (String field: exportFields){
                    if (fieldParamMap.containsKey(field)){
                        Object v = data.get(field);
                        String normalValue = v == null ? "" : String.valueOf(v);
                        FieldParam fieldParam = fieldParamMap.get(field);
                        FieldTypeEnums fieldType = FieldTypeEnums.formatOrNull(fieldParam.getField().getType());
                        if (fieldType == FieldTypeEnums.USER || fieldType == FieldTypeEnums.DEPT || fieldType == FieldTypeEnums.TREE_SELECT){
                            if (v instanceof Member){
                                writeData.add(((Member) v).getName());
                            }else if (v instanceof Collection){
                                StringBuilder builder = new StringBuilder();
                                for (Object subV: (Collection) v){
                                    if (subV instanceof Member){
                                        builder.append(((Member) subV).getName()).append(",");
                                    }
                                }
                                if (builder.length() > 0){
                                    builder.deleteCharAt(builder.length() - 1);
                                }
                                writeData.add(builder.toString());
                            }else{
                                writeData.add(normalValue);
                            }
                        }else if (fieldType == FieldTypeEnums.SELECT || fieldType == FieldTypeEnums.MULTISELECT){
                            Map<Object, Object> options = HeaderUtil.parseSelectOptions(fieldParam);
                            if (fieldType == FieldTypeEnums.SELECT){
                                if (options.get(v) == null){
                                    writeData.add("");
                                }else{
                                    writeData.add(options.get(v));
                                }
                            }else{
                                List<Object> objs = new ArrayList<>();
                                if (v instanceof Collection){
                                    for (Object id: (Collection)v){
                                        if (options.get(id) != null){
                                            objs.add(options.get(id));
                                        }
                                    }
                                }
                                writeData.add(StringUtils.join(objs.toArray(), ","));
                            }
                        }else{
                            writeData.add(normalValue);
                        }
                    }
                }
                writeDatas.add(writeData);
            }
            excelWriterSheetBuilder.doWrite(writeDatas);
        }
        return "";
    }

    public List<FieldParam> exportFields(Long orgId, Long appId){
        BizForm bizForm = summaryService.getBizForm(orgId, appId);
        if (bizForm == null){
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        List<FieldParam> fieldParams = bizForm.getFieldList();
//        fieldParams.addAll(FormFieldConstant.getCommonFields().values());
        fieldParams.removeIf(f -> ! FormFieldConstant.supportedImport(f.getField().getType()));
//        fieldParams.removeIf(f -> Objects.equals(f.getName(), FormFieldConstant.STATUS) || Objects.equals(f.getName(), FormFieldConstant.APP_ID));
        return fieldParams;
    }

    public String userExport(Long orgId, Long userId, OutputStream outputStream) {
        List<List<String>> headers = new ArrayList<>();
        headers.add(Collections.singletonList("类型"));
        headers.add(Collections.singletonList("id"));
        headers.add(Collections.singletonList("名称"));
        headers.add(Collections.singletonList("父级信息"));

        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.write(outputStream).head(headers).sheet("成员信息");

        GetRepeatMemberReq getMemberSimpleInfoReq = new GetRepeatMemberReq();
        getMemberSimpleInfoReq.setOrgId(orgId);
        RepeatMemberInfoResp data = userCenterApi.getRepeatMember(getMemberSimpleInfoReq).getData();
        List<List<Object>> writeDatas = new ArrayList<>();

        dealRepeatData(writeDatas, data.getUser(), "用户");
        dealRepeatData(writeDatas, data.getDepartment(), "部门");
        dealRepeatData(writeDatas, data.getRole(), "角色");
        excelWriterSheetBuilder.doWrite(writeDatas);
        return "";
    }

    private List<List<Object>> dealRepeatData(List<List<Object>> writeDatas, List<RepeatMemberInfo> data, String type) {
        if (data.isEmpty()) {
            return writeDatas;
        }
        for (RepeatMemberInfo datum : data) {
            List<Object> writeData = new ArrayList<>();
            writeData.add(type);
            writeData.add(String.valueOf(datum.getId()));
            writeData.add(datum.getName());
            String otherInfo = "";
            if (!datum.getDepartment().isEmpty()) {
                for (String s : datum.getDepartment()) {
                    otherInfo += s + ",";
                }
                otherInfo = otherInfo.substring(0, otherInfo.length() - 1);
            }

            writeData.add(otherInfo);
            writeDatas.add(writeData);
        }

        return writeDatas;
    }

    public String exportTemplate(Long orgId, Long userId, OutputStream outputStream, ExportTemplateReq req) {
        AppResp app = appProvider.getAppInfo(orgId, req.getAppId()).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        List<FieldParam> fieldParams = exportFields(orgId, app.getId());
        Map<String, FieldParam> fieldParamMap = MapUtils.toMap(FieldParam::getName, fieldParams);
        List<String> exportFields = fieldParams.stream().map(FieldParam::getName).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(exportFields)){
            throw new BusinessException(ResultCode.EXPORT_FIELDS_IS_EMPTY);
        }

        List<List<String>> headers = new ArrayList<>();
        for (String field: exportFields){
            String label = fieldParamMap.get(field).getLabel();
            headers.add(Collections.singletonList(label));
        }
        EasyExcel.write(outputStream).head(headers).sheet(app.getName().replaceAll("/", "_")).doWrite(null);
        return "";
    }


}
