package com.polaris.lesscode.form.internal.api;


import com.polaris.lesscode.form.internal.req.AppValueListReq;
import com.polaris.lesscode.form.internal.req.QuerySqlReq;
import com.polaris.lesscode.form.internal.resp.QuerySqlResp;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value="应用表单管理（内部调用）", tags={"应用表单管理（内部调用）"})
@RequestMapping("/form/inner/api/v1")
public interface AppFormApi {

    @ApiOperation(value="获取应用数据", notes="获取应用数据")
    @PostMapping("/querySql")
    Result<QuerySqlResp> querySql(@Validated @RequestBody QuerySqlReq req);

//
//	/**
//	 * 公共查询表单接口
//	 *
//	 * @param filter
//	 * @return
//	 */
//	@ApiOperation(value = "公共查询表单接口", notes = "公共查询表单接口")
//	@PostMapping("/forms/filter")
//	Result<List<AppFormResp>> filter(@RequestBody AppFormFilter filter);
//
//	/**
//	 * 获取应用表单
//	 *
//	 * @param appId 应用id
//	 * @return
//	 */
//	@ApiOperation(value = "获取应用表单", notes = "获取应用表单")
//	@GetMapping("/forms/get-form-by-appId")
//	Result<AppFormResp> getFormByAppId(@RequestParam("appId") Long appId);
//
//	/**
//	 * 获取应用表单
//	 *
//	 * @param orgId
//	 * @param appId
//	 * @return
//	 */
//	@ApiOperation(value = "获取应用表单", notes = "获取应用表单")
//	@GetMapping("/forms/get-form-by-appId-orgId")
//	Result<AppFormResp> getFormByAppId(@RequestParam("orgId") Long orgId, @RequestParam("appId") Long appId);
//
//	/**
//	 * 保存应用表单
//	 *
//	 * @param req 请求结构体
//	 * @return
//	 */
//	@ApiOperation(value = "保存应用表单", notes = "保存应用表单")
//	@PostMapping("/forms/save-form")
//	Result<?> saveForm(@RequestParam(value = "pkgId", required = false) Long pkgId, @RequestParam(value = "type", required = false) Integer type, @RequestBody AppFormSaveReq req);
//
//	/**
//	 * 获取表单配置接口
//	 *
//	 **/
//	@ApiOperation("获取表单配置")
//	@GetMapping("/forms/config")
//	Result<AppFormConfigResp> getFormConfig(@RequestParam("appId") Long appId, @RequestParam("orgId") Long orgId);
//
//	/**
//	 * 批量获取表单配置接口
//	 *
//	 **/
//	@ApiOperation("获取表单配置")
//	@PostMapping("/forms/configs")
//	Result<List<AppFormConfigResp>> getFormConfigs(@RequestBody GetFormConfigsReq req);
//
//	/**
//	 * 获取业务表单配置
//	 *
//	 * @Author Nico
//	 * @Date 2021/6/23 11:21
//	 **/
//	@ApiOperation("获取业务表单配置")
//	@GetMapping("/forms/get-biz-form")
//	Result<BizForm> getBizForm(@RequestParam("orgId") Long orgId, @RequestParam("appId") Long appId);
//
//	/**
//	 * 批量获取业务表单配置
//	 *
//	 * @Author Nico
//	 * @Date 2021/6/23 11:21
//	 **/
//	@ApiOperation("批量获取业务表单配置")
//	@PostMapping("/forms/get-biz-forms")
//	Result<List<BizForm>> getBizForms(@RequestBody GetBizFormListReq req);
//
//	/**
//	 * 批量获取应用表单
//	 *
//	 * @param appIds 应用ids
//	 * @return
//	 */
//	@ApiOperation(value = "批量获取应用表单", notes = "批量获取应用表单")
//	@PostMapping("/forms/get-form-by-appIds")
//	Result<List<AppFormResp>> getFormByAppIds(@RequestBody List<Long> appIds);
//
//	/**
//	 * 获取应用表单字段信息
//	 *
//	 * @param appId
//	 * @return
//	 */
//	@ApiOperation(value = "获取应用表单字段信息", notes = "获取应用表单字段信息")
//	@GetMapping("/forms/fields")
//	Result<List<FormFieldResp>> getFields(@RequestParam("appId") Long appId);
//
//	/**
//	 * 删除应用表单
//	 *
//	 * @param appIds
//	 * @return
//	 */
//	@ApiOperation(value = "删除应用表单", notes = "删除应用表单")
//	@GetMapping("/forms/delete")
//	Result<Integer> deleteForm(@RequestParam("appIds") List<Long> appIds, @RequestParam("userId") Long userId);
//
//
//	@ApiOperation(value = "修改组织字段", notes = "修改组织字段")
//	@PostMapping("/baseforms/save")
//	Result<Boolean> saveBaseFields(@RequestBody AppFormBaseSaveReq internalReq);

}
