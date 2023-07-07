package com.polaris.lesscode.form.internal.api;


import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.form.internal.req.*;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value="应用数据增删改查（内部调用）", tags={"应用数据增删改查（内部调用）"})
@RequestMapping("/form/inner/api/v1/apps/{appId}/values")
public interface AppValueApi {

	@ApiOperation(value="获取应用数据", notes="获取应用数据")
	@PostMapping("/filter")
	Result<Page<Map<String, Object>>> filter(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueListReq req);

	@ApiOperation(value="获取应用数据裸数据", notes="获取应用数据裸数据")
	@PostMapping("/filterRaw")
	Result<List<Map<String, Object>>> filterRaw(@Validated @RequestBody AppValueListReq req);

	@ApiOperation(value="自定义统计应用数据", notes="自定义统计应用数据")
	@PostMapping("/filterCustomStat")
	Result<List<Map<String, Object>>> filterCustomStat(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueListReq req);

	@ApiOperation(value="统计应用数据", notes="统计应用数据")
	@PostMapping("/filterStat")
	Result<Long> filterStat(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueListReq req);

	@ApiOperation(value="添加应用数据", notes="添加应用数据")
	@PostMapping
	Result<?> add(@PathVariable("appId") Long appId, @Validated @RequestBody AppValueAddReq req);

	@ApiOperation(value = "更新应用数据", notes = "更新应用数据")
	@PutMapping
	Result<List<Map<String, Object>>> update(@PathVariable("appId") Long appId, @RequestBody AppValueUpdateReq req);

	@ApiOperation(value = "(批量)更新应用数据", notes = "更新应用数据")
	@PostMapping("/updateBatchRaw")
	Result<Boolean> updateBatchRaw(@PathVariable("appId") Long appId, @RequestBody AppValueUpdateBatchReq req);

	@ApiOperation(value = "删除应用数据", notes = "删除应用数据")
	@PostMapping("/delete")
	Result<Boolean> del(@PathVariable("appId") Long appId, @RequestBody AppValueDeleteReq req);

	@ApiOperation(value = "回收主表数据", notes = "回收主表数据")
	@PutMapping("/recycle")
	Result<Integer> recycle(@PathVariable("appId") Long appId, @RequestBody AppRecycleReq req);

	@ApiOperation(value = "恢复主表数据", notes = "恢复主表数据")
	@PostMapping("/recover")
	Result<Integer> recover(@PathVariable("appId") Long appId, @RequestBody AppRecycleReq req);

//	@ApiOperation(value="添加子表数据", notes="添加子表数据")
//	@PostMapping("/{dataId}/subforms/{fieldKey}")
//	Result<?> addSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @Validated @RequestBody SubValueAddReq req);
//
//	@ApiOperation(value="编辑子表数据", notes="编辑子表数据")
//	@PutMapping("/{dataId}/subforms/{fieldKey}")
//	Result<?> updateSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @Validated @RequestBody SubValueUpdateReq req);
//
//	@ApiOperation(value="删除子表数据", notes="删除子表数据")
//	@DeleteMapping("/{dataId}/subforms/{fieldKey}")
//	Result<?> deleteSubValues(@PathVariable("appId") Long appId, @PathVariable("dataId") Long dataId, @PathVariable("fieldKey") String fieldKey, @ Validated @RequestBody SubValueDeleteReq req);

	@ApiOperation(value = "移动数据", notes = "移动数据")
	@PutMapping("/moving")
	Result<Map<Long, Double>> movingData(@PathVariable("appId") Long appId, @RequestBody AppValueMovingReq req);
}
