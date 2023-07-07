package com.polaris.lesscode.form.openapi.controller;

import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.openapi.model.AddValuesReq;
import com.polaris.lesscode.form.openapi.model.FilterValuesReq;
import com.polaris.lesscode.form.openapi.model.UpdateValuesReq;
import com.polaris.lesscode.form.req.*;
import com.polaris.lesscode.form.resp.AppImportResp;
import com.polaris.lesscode.form.resp.AppValueResp;
import com.polaris.lesscode.form.service.*;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Api(tags="表数据开放API")
@Validated
@RestController
@RequestMapping("/form/open/v1/apps/{appId}/values")
public class AppOpenValueController {

	@Autowired
	private DataAddService dataAddService;

	@Autowired
	private DataFilterService dataFilterService;

	@Autowired
	private DataDeleteService dataDeleteService;

	@Autowired
	private DataUpdateService dataUpdateService;

	@ApiOperation(value="获取应用数据", notes="获取应用数据")
	@PostMapping("/filter")
	public Result<Page<Map<String, Object>>> filter(@PathVariable("appId") Long appId, @RequestBody FilterValuesReq req) {
		AppValueListReq appValueListReq = new AppValueListReq();
		appValueListReq.setPage(req.getPage());
		appValueListReq.setSize(req.getSize());
		appValueListReq.setColumns(req.getFields());
		appValueListReq.setOrders(req.getSort());
		appValueListReq.setCondition(req.getFilter());
		Page<Map<String, Object>> resp = dataFilterService.filterInternal(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, appValueListReq);
		return Result.ok(resp);
	}
	
	@ApiOperation(value="添加数据", notes="添加数据")
	@PostMapping
	public Result<Collection<Map<String, Object>>> add(@PathVariable("appId") Long appId, @RequestBody AddValuesReq req){
		AppValueAddReq appValueAddReq = new AppValueAddReq();
		appValueAddReq.setForm(req.getValues());
		appValueAddReq.setBeforeId(req.getBeforeId());
		appValueAddReq.setAfterId(req.getAfterId());
		return Result.ok(dataAddService.add(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, appValueAddReq, true, false, false));
	}

	@ApiOperation(value="更新数据", notes="更新数据")
	@PutMapping
	public Result<Collection<Map<String, Object>>> update(@PathVariable("appId") Long appId, @RequestBody UpdateValuesReq req) {
		AppValueUpdateReq appValueUpdateReq = new AppValueUpdateReq();
		appValueUpdateReq.setForm(req.getValues());
		return Result.ok(dataUpdateService.update(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, appValueUpdateReq, true, false));
	}

	@ApiOperation(value="删除数据", notes="删除数据")
	@DeleteMapping
	public Result<Boolean> del(@PathVariable("appId") Long appId, @RequestParam("ids") List<Long> ids) {
		return Result.ok(dataDeleteService.delete(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, ids, true));
	}

}
