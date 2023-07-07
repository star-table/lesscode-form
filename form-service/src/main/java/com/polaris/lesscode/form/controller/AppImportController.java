package com.polaris.lesscode.form.controller;

import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.ImportProgress;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.req.*;
import com.polaris.lesscode.form.resp.ImportPreResp;
import com.polaris.lesscode.form.resp.ImportValidateResp;
import com.polaris.lesscode.form.service.DataExportService;
import com.polaris.lesscode.form.service.DataImportService;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Api(tags="应用数据导入")
@Validated
@RestController
@RequestMapping("/form/api/v1")
public class AppImportController {

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private DataExportService dataExportService;

    @Autowired
    private AppProvider appProvider;

    @ApiOperation(value = "预导入", notes = "预导入")
    @PostMapping("/pre-import")
    public Result<ImportPreResp> importPre(@RequestBody ImportPreReq req) throws IOException {
        return Result.ok(dataImportService.importPre(RequestContext.currentOrgId(), RequestContext.currentUserId(), req));
    }

    @ApiOperation(value = "导入", notes = "导入")
    @PostMapping("/import")
    public Result<?> importData(@RequestBody ImportDataReq req) throws IOException {
        dataImportService.importData(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok();
    }

    @ApiOperation(value = "导入信息刷新", notes = "导入信息刷新")
    @PostMapping("/import-refresh")
    public Result<ImportPreResp> importRefresh(@RequestBody ImportRefreshReq req) throws IOException {
        return Result.ok(dataImportService.importRefresh(RequestContext.currentOrgId(), RequestContext.currentUserId(), req));
    }

    @ApiOperation(value = "导入校验", notes = "导入校验")
    @PostMapping("/import-validate")
    public Result<ImportValidateResp> importValidate(@RequestBody ImportValidateReq req) throws IOException {
        return Result.ok(dataImportService.importValidate(RequestContext.currentOrgId(), RequestContext.currentUserId(), req));
    }

    @ApiOperation(value = "导入进度", notes = "导入进度")
    @GetMapping("/import-progress")
    public Result<ImportProgress> importProcess(@RequestParam String token) {
        return Result.ok(dataImportService.importProgress(token));
    }

    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/template")
    public void template(@RequestBody ExportTemplateReq req, HttpServletResponse response){
        AppResp app = appProvider.getAppInfo(RequestContext.currentOrgId(), req.getAppId()).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        OutputStream outputStream = null;
        try {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(app.getName().replaceAll("/", "_").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xlsx");
            outputStream = response.getOutputStream();
            dataExportService.exportTemplate(RequestContext.currentOrgId(), RequestContext.currentUserId(), outputStream, req);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @ApiOperation(value = "导出", notes = "导出")
    @PostMapping("/export")
    public void export(@RequestBody ExportDataReq req, HttpServletResponse response) {
        AppResp app = appProvider.getAppInfo(RequestContext.currentOrgId(), req.getAppId()).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        OutputStream outputStream = null;
        try {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(app.getName().replaceAll("/", "_").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xlsx");
            outputStream = response.getOutputStream();
            dataExportService.export(RequestContext.currentOrgId(), RequestContext.currentUserId(), outputStream, req);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @ApiOperation("获取表单可导出字段")
    @GetMapping("/export/fields")
    public Result<List<FieldParam>> exportFields(@RequestParam Long appId){
        return Result.ok(dataExportService.exportFields(RequestContext.currentOrgId(), appId));
    }

    @ApiOperation(value = "重名成员/部门ID下载", notes = "重名成员/部门ID下载")
    @PostMapping("user/export")
    public void userExport(HttpServletResponse response) {
        OutputStream outputStream = null;
        try {
            response.setContentType("application/force-download");
            String title = "成员/部门ID";
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(title.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xlsx");
            outputStream = response.getOutputStream();
            dataExportService.userExport(RequestContext.currentOrgId(), RequestContext.currentUserId(), outputStream);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
