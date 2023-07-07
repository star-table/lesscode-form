package com.polaris.lesscode.form.internal.controller;

import com.polaris.lesscode.app.internal.enums.ActionObjType;
import com.polaris.lesscode.app.internal.enums.ActionType;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.form.annotations.ActionLogging;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.req.*;
import com.polaris.lesscode.form.service.*;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AppValueInternalController implements AppValueApi {

	@Autowired
	private DataFilterService dataFilterService;

	@Autowired
	private DataDeleteService dataDeleteService;

	@Autowired
	private DataAddService dataAddService;

	@Autowired
	private DataUpdateService dataUpdateService;

	@Autowired
	private DataUpdateBatchService dataUpdateBatchService;

	@Autowired
	private DataUnitService dataUnitService;

	@Override
	public Result<Page<Map<String, Object>>> filter(Long appId, AppValueListReq internalReq) {
		com.polaris.lesscode.form.req.AppValueListReq req = new com.polaris.lesscode.form.req.AppValueListReq();
		req.setCondition(internalReq.getCondition());
		req.setConditions(internalReq.getConditions());
		req.setColumns(internalReq.getColumns());
		req.setGroups(internalReq.getGroups());
		req.setOrders(internalReq.getOrders());
		req.setPage(internalReq.getPage());
		req.setSize(internalReq.getSize());
		req.setRedirectIds(internalReq.getRedirectIds());
		req.setFilterColumns(internalReq.getFilterColumns());
		req.setTableId(internalReq.getTableId());
		req.setNeedTotal(internalReq.isNeedTotal());
		req.setExport(internalReq.isExport());
		req.setNeedRefColumn(internalReq.isNeedRefColumn());
		req.setAggNoLimit(internalReq.isAggNoLimit());
		req.setNeedDeleteData(internalReq.isNeedDeleteData());
		Page<Map<String, Object>> resp = dataFilterService.filterInternal(internalReq.getOrgId(), internalReq.getUserId(), appId, req);
		return Result.ok(resp);
	}

	@Override
	public Result<List<Map<String, Object>>> filterRaw(AppValueListReq internalReq) {
		com.polaris.lesscode.form.req.AppValueListReq req = new com.polaris.lesscode.form.req.AppValueListReq();
		req.setCondition(internalReq.getCondition());
		req.setConditions(internalReq.getConditions());
		req.setColumns(internalReq.getColumns());
		req.setGroups(internalReq.getGroups());
		req.setOrders(internalReq.getOrders());
		req.setPage(internalReq.getPage());
		req.setSize(internalReq.getSize());
		req.setRedirectIds(internalReq.getRedirectIds());
		req.setFilterColumns(internalReq.getFilterColumns());
		req.setTableId(internalReq.getTableId());
		List<Map<String, Object>> resp = dataFilterService.filterInternalRaw(internalReq.getOrgId(), internalReq.getUserId(), req);
		return Result.ok(resp);
	}

	@Override
	public Result<List<Map<String, Object>>> filterCustomStat(Long appId, AppValueListReq internalReq) {
		com.polaris.lesscode.form.req.AppValueListReq req = new com.polaris.lesscode.form.req.AppValueListReq();
		req.setCondition(internalReq.getCondition());
		req.setConditions(internalReq.getConditions());
		req.setColumns(internalReq.getColumns());
		req.setGroups(internalReq.getGroups());
		req.setOrders(internalReq.getOrders());
		req.setPage(internalReq.getPage());
		req.setSize(internalReq.getSize());
		req.setRedirectIds(internalReq.getRedirectIds());
		req.setFilterColumns(internalReq.getFilterColumns());
		req.setTableId(internalReq.getTableId());
		List<Map<String, Object>> resp = dataFilterService.filterInternalCustomStat(internalReq.getOrgId(), internalReq.getUserId(), appId, req);
		return Result.ok(resp);
	}

	@Override
	public Result<Long> filterStat(Long appId, AppValueListReq internalReq) {
		com.polaris.lesscode.form.req.AppValueListReq req = new com.polaris.lesscode.form.req.AppValueListReq();
		req.setCondition(internalReq.getCondition());
		req.setConditions(internalReq.getConditions());
		req.setColumns(internalReq.getColumns());
		req.setGroups(internalReq.getGroups());
		req.setOrders(internalReq.getOrders());
		req.setPage(1);
		req.setSize(1);
		req.setRedirectIds(internalReq.getRedirectIds());
		req.setTableId(internalReq.getTableId());
		req.setNeedTotal(true);
		Long resp = dataFilterService.filterInternalStat(internalReq.getOrgId(), internalReq.getUserId(), appId, req);
		return Result.ok(resp);
	}

//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.CREATE,
//			dataExpress = "${req.form}"
//	)
	@Override
	public Result<?> add(Long appId, AppValueAddReq internalReq) {
		com.polaris.lesscode.form.req.AppValueAddReq req = new com.polaris.lesscode.form.req.AppValueAddReq();
		req.setAsc(req.isAsc());
		req.setAfterId(internalReq.getAfterId());
		req.setBeforeId(internalReq.getBeforeId());
		req.setForm(internalReq.getForm());
		req.setRedirectIds(internalReq.getRedirectIds());
		req.setTableId(internalReq.getTableId());
		return Result.ok(dataAddService.add(internalReq.getOrgId(), internalReq.getUserId(), appId, req, true, internalReq.isImport(), internalReq.isCreateTemplate()));
	}

//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.MODIFY,
//			dataExpress = "${req.form}"
//	)
	@Override
	public Result<List<Map<String, Object>>> update(Long appId, AppValueUpdateReq req) {
		com.polaris.lesscode.form.req.AppValueUpdateReq appValueUpdateReq = new com.polaris.lesscode.form.req.AppValueUpdateReq();
		appValueUpdateReq.setForm(req.getForm());
		appValueUpdateReq.setRedirectIds(req.getRedirectIds());
		appValueUpdateReq.setTableId(req.getTableId());
		return Result.ok(dataUpdateService.update(req.getOrgId(), req.getUserId(), appId, appValueUpdateReq, true, false));
	}

	@Override
	public Result<Boolean> updateBatchRaw(Long appId, AppValueUpdateBatchReq req) {
		com.polaris.lesscode.form.req.AppValueUpdateBatchReq appValueUpdateBatchReq = new com.polaris.lesscode.form.req.AppValueUpdateBatchReq();
		appValueUpdateBatchReq.setCondition(req.getCondition());
		appValueUpdateBatchReq.setSets(req.getSets());
		return Result.ok(dataUpdateBatchService.updateBatchRaw(req.getOrgId(), req.getUserId(), appValueUpdateBatchReq));
	}

//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.DELETE,
//			dataIdExpress = "${req.appValueIds}"
//	)
	@Override
	public Result<Boolean> del(Long appId, AppValueDeleteReq req) {
		if (CollectionUtils.isNotEmpty(req.getIssueIds())){
			req.setAppValueIds(dataUnitService.getDataIdsByIssueIds(req.getOrgId(), appId, req.getIssueIds()));
		}
		boolean suc = dataDeleteService.delete(req.getOrgId(), req.getUserId(), appId, req.getAppValueIds(), true);
		return Result.ok(suc);
	}

//	@ApiOperation(value = "回收主表数据", notes = "回收主表数据")
//	@PutMapping("/recycle")
//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.RECYCLE,
//			dataIdExpress = "${dataIds}"
//	)
	@Override
	public Result<Integer> recycle(Long appId, @RequestBody AppRecycleReq req){
		return Result.ok(dataUpdateService.recycle(req.getOrgId(), req.getUserId(), req.getAppId(), req.getTableId(), req.getDataIds(), req.getIssueIds(), true));
	}

//	@ApiOperation(value = "恢复主表数据", notes = "恢复主表数据")
//	@PostMapping("/recover")
//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.RECOVER,
//			dataIdExpress = "${dataIds}"
//	)
	@Override
	public Result<Integer> recover(Long appId, @RequestBody AppRecycleReq req){
		return Result.ok(dataUpdateService.recover(req.getOrgId(), req.getUserId(), req.getAppId(), req.getTableId(), req.getDataIds(),req.getIssueIds(), true));
	}


//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.CREATE,
//			dataIdExpress = "${dataId}",
//			dataExpress = "${req.subform}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	@Override
//	public Result<?> addSubValues(Long appId, Long dataId, String fieldKey, SubValueAddReq req) {
//		return Result.ok(dataAddService.addSub(req.getOrgId(), req.getUserId(), appId, dataId, fieldKey, req.getSubform()));
//	}

//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.MODIFY,
//			dataIdExpress = "${dataId}",
//			dataExpress = "${req.subform}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	@Override
//	public Result<?> updateSubValues(Long appId, Long dataId, String fieldKey, SubValueUpdateReq req) {
//		return Result.ok(dataUpdateService.updateSub(req.getOrgId(), req.getUserId(), appId, dataId, fieldKey, req.getSubform()));
//	}

//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.DELETE,
//			dataIdExpress = "${dataId}",
//			subDataIdExpress = "${req.subformValueIds}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	@Override
//	public Result<?> deleteSubValues(Long appId, Long dataId, String fieldKey, SubValueDeleteReq req) {
//		Boolean suc = dataDeleteService.deleteSub(req.getOrgId(), req.getUserId(), appId, dataId, fieldKey, req.getSubformValueIds());
//		return Result.ok(suc);
//	}

	@Override
	public Result<Map<Long, Double>> movingData(Long appId, AppValueMovingReq req) {
		com.polaris.lesscode.form.req.AppValueMovingReq movingReq = new com.polaris.lesscode.form.req.AppValueMovingReq();
		movingReq.setAsc(req.isAsc());
		movingReq.setAfterId(req.getAfterId());
		movingReq.setBeforeId(req.getBeforeId());
		movingReq.setDataId(req.getDataId());
		movingReq.setSubformKey(req.getSubformKey());
		return Result.ok(dataUpdateService.moving(req.getOrgId(), req.getUserId(), appId, movingReq, true));
	}
}
