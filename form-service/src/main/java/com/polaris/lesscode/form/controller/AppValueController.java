package com.polaris.lesscode.form.controller;

import com.polaris.lesscode.app.internal.enums.ActionObjType;
import com.polaris.lesscode.app.internal.enums.ActionType;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.annotations.ActionLogging;
import com.polaris.lesscode.form.req.*;
import com.polaris.lesscode.form.service.*;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags="应用数据管理")
@Validated
@RestController
@RequestMapping("/form/api/v1/apps/{appId}/values")
public class AppValueController {

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

	@ApiOperation(value="获取应用数据", notes="获取应用数据")
	@PostMapping("/filter")
	public Result<Page<Map<String, Object>>> filter(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueListReq req) {
		Page<Map<String, Object>> resp = dataFilterService.filter(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req);
		return Result.ok(resp);
	}

	@ApiOperation(value="单行数据查询自动补全", notes="单行数据查询自动补全")
	@GetMapping("/auto-complete")
	public Result<List<Map<String, Object>>> autoComplete(
			@ApiParam("应用id") @PathVariable("appId") Long appId,
			@ApiParam("查询的字段key") @RequestParam("key") String key,
			@ApiParam("查询条件") @RequestParam("query") String query) {
		return Result.ok(dataUnitService.autoComplete(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, key, query));
	}

	@ApiOperation(value="模糊匹配", notes="模糊匹配")
	@GetMapping("/fuzzy")
	public Result<List<Map<String, Object>>> fuzzy(
			@ApiParam("应用id") @PathVariable("appId") Long appId,
			@ApiParam("查询的字段key") @RequestParam("key") String key,
			@ApiParam("查询条件") @RequestParam("query") String query) {
		return Result.ok(dataUnitService.fuzzy(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, key, query));
	}

	@ApiOperation(value="唯一性检测", notes="唯一性检测")
	@PostMapping("/check-unique")
	public Result<Boolean> checkUnique(
			@RequestBody CheckUniqueReq req) {
		return Result.ok(dataUnitService.checkUnique(RequestContext.currentOrgId(), RequestContext.currentUserId(), req.getAppId(), req.getDataId(), req.getSubformKey(), req.getKey(), req.getValue()));
	}

	@ApiOperation(value="添加应用数据", notes="添加应用数据")
	@PostMapping
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.CREATE,
			dataExpress = "${req.form}"
	)
	public Result<?> add(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueAddReq req){
		return Result.ok(dataAddService.add(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req));
	}

	@ApiOperation(value = "更新应用数据", notes = "更新应用数据")
	@PutMapping
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.MODIFY,
			dataExpress = "${req.form}"
	)
	public Result<List<Map<String, Object>>> update(@PathVariable("appId") Long appId, @RequestBody AppValueUpdateReq req) {
		return Result.ok(dataUpdateService.update(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req));
	}

	@ApiOperation(value = "删除应用数据", notes = "删除应用数据")
	@PostMapping("/delete")
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.DELETE,
			dataIdExpress = "${req.appValueIds}"
	)
	public Result<Boolean> del(@PathVariable("appId") Long appId, @RequestBody AppValueDeleteReq req) {
		boolean suc = dataDeleteService.delete(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req.getAppValueIds());
		return Result.ok(suc);
	}

//	@ApiOperation(value="添加子表数据", notes="添加子表数据")
//	@PostMapping("/{dataId}/subforms/{fieldKey}")
//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.CREATE,
//			dataIdExpress = "${dataId}",
//			dataExpress = "${req.subform}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	public Result<?> addSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @Validated @RequestBody SubValueAddReq req){
//		return Result.ok(dataAddService.addSub(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, dataId, fieldKey, req.getSubform()));
//	}

//	@ApiOperation(value="编辑子表数据", notes="编辑子表数据")
//	@PutMapping("/{dataId}/subforms/{fieldKey}")
//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.MODIFY,
//			dataIdExpress = "${dataId}",
//			dataExpress = "${req.subform}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	public Result<?> updateSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @Validated @RequestBody SubValueUpdateReq req){
//		return Result.ok(dataUpdateService.updateSub(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, dataId, fieldKey, req.getSubform()));
//	}

//	@ApiOperation(value="删除子表数据", notes="删除子表数据")
//	@DeleteMapping("/{dataId}/subforms/{fieldKey}")
//	@ActionLogging(
//			appIdExpress = "${appId}",
//			objType = ActionObjType.FORM,
//			action = ActionType.DELETE,
//			dataIdExpress = "${dataId}",
//			subDataIdExpress = "${req.subformValueIds}",
//			subformKeyExpress = "${fieldKey}"
//	)
//	public Result<?> deleteSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @ Validated @RequestBody SubValueDeleteReq req){
//		Boolean suc = dataDeleteService.deleteSub(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, dataId, fieldKey, req.getSubformValueIds());
//		return Result.ok(suc);
//	}

	@ApiOperation(value = "复制列数据", notes = "复制列数据")
	@PostMapping("/copy-column")
	public Result<?> copyColumn(@PathVariable("appId") Long appId, @RequestBody CopyColumnValueReq req) {
		return Result.ok(dataUnitService.copyColumn(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req));
	}

	@ApiOperation(value = "回收主表数据", notes = "回收主表数据")
	@PutMapping("/recycle")
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.RECYCLE,
			dataIdExpress = "${dataIds}"
	)
	public Result<Integer> recycle(@PathVariable("appId") Long appId, @RequestBody List<Long> dataIds){
		return Result.ok(dataUpdateService.recycle(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, 0L, dataIds,null, false));
	}

	@ApiOperation(value = "恢复主表数据", notes = "恢复主表数据")
	@PostMapping("/recover")
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.RECOVER,
			dataIdExpress = "${dataIds}"
	)
	public Result<Integer> recover(@PathVariable("appId") Long appId, @RequestBody List<Long> dataIds){
		return Result.ok(dataUpdateService.recover(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId,0L, dataIds, null, false));
	}

	@ApiOperation(value = "启用数据", notes = "启用数据")
	@PutMapping("/enable")
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.ENABLE,
			dataIdExpress = "${dataIds}"
	)
	public Result<Integer> enableData(@PathVariable("appId") Long appId,
											   @RequestBody List<Long> dataIds){
		return Result.ok(dataUpdateService.enable(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, dataIds, false));
	}

	@ApiOperation(value = "禁用数据", notes = "禁用数据")
	@PutMapping("/disable")
	@ActionLogging(
			appIdExpress = "${appId}",
			objType = ActionObjType.FORM,
			action = ActionType.DISABLE,
			dataIdExpress = "${dataIds}"
	)
	public Result<Integer> disableData(@PathVariable("appId") Long appId,
											   @RequestBody List<Long> dataIds){
		return Result.ok(dataUpdateService.disable(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, dataIds, false));
	}

	@ApiOperation(value = "移动数据", notes = "移动数据")
	@PutMapping("/moving")
	public Result<Map<Long, Double>> movingData(@PathVariable("appId") Long appId, @RequestBody AppValueMovingReq req){
		return Result.ok(dataUpdateService.moving(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req, false));
	}

}
