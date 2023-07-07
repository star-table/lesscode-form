package com.polaris.lesscode.form.controller;

import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.form.service.AppMemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: Liu.B.J
 * @data: 2020/10/29 15:00
 * @description:
 */
@Api(tags="白名单管理")
@RestController
@RequestMapping("/form/white-api/v1/{appId}")
public class WhiteListController {

    @Autowired
    private AppMemoService appMemoService;

    @ApiOperation(value="下载备忘录pdf", notes="下载备忘录pdf")
    @GetMapping(value = "/memo/download-pdf")
    public void downloadPdf(@PathVariable("appId") Long appId, @RequestParam("memoId") Long memoId, HttpServletResponse response) throws Exception {
        appMemoService.downloadPdf(RequestContext.currentOrgId(), appId, memoId, response);
    }

}
