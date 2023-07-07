package com.polaris.lesscode.form.controller;

import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.form.req.AppMemoValueAddReq;
import com.polaris.lesscode.form.req.AppMemoValueDeleteReq;
import com.polaris.lesscode.form.req.AppMemoValueListReq;
import com.polaris.lesscode.form.req.AppMemoValueUpdateReq;
import com.polaris.lesscode.form.resp.AppMemoResp;
import com.polaris.lesscode.form.service.AppMemoService;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="员工备忘录管理")
@RestController
@RequestMapping("/form/api/v1/{appId}/memo")
public class AppMemoController {

	@Autowired
	private AppMemoService appMemoService;

	/*@ApiOperation(value="上传图片", notes="上传图片")
	@PostMapping("/upload-image")
	public Result<Map> uploadImage(@PathVariable("appId") Long appId, @RequestParam("image") MultipartFile image){
		return Result.ok(appMemoService.uploadImage(appId, image));
	}*/

	/*@ApiOperation(value="下载备忘录word", notes="下载备忘录word")
	@GetMapping("/download-word")
	public Result<Map> downloadWord(@PathVariable("appId") Long appId, @RequestParam("memoId") Long memoId){
		return Result.ok(appMemoService.downloadWord(appId, memoId));
	}*/

	@ApiOperation(value="获取备忘录数据", notes="获取备忘录数据")
	@PostMapping("/filter")
	public Result<Page<AppMemoResp>> filter(@PathVariable("appId") Long appId, @Validated @RequestBody AppMemoValueListReq req){
		Page<AppMemoResp> resps = appMemoService.filter(RequestContext.currentOrgId(), appId, RequestContext.currentUserId(), req);
		return Result.ok(resps);
	}

	@ApiOperation(value="添加备忘录数据", notes="添加备忘录数据")
	@PostMapping("/add")
	public Result<?> add(@PathVariable("appId") Long appId, @Validated @RequestBody AppMemoValueAddReq req){
		appMemoService.add(RequestContext.currentOrgId(), appId, RequestContext.currentUserId(), req);
		return Result.ok();
	}

	@ApiOperation(value="更新备忘录数据", notes="更新备忘录数据")
	@PutMapping("/modify")
	public Result<AppMemoResp> update(@PathVariable("appId") Long appId, @Validated @RequestBody AppMemoValueUpdateReq req){
		return Result.ok(appMemoService.update(RequestContext.currentOrgId(), appId, RequestContext.currentUserId(), req));
	}

	@ApiOperation(value="删除备忘录数据", notes="删除备忘录数据")
	@PostMapping("/delete")
	public Result<Integer> del(@PathVariable("appId") Long appId, @Validated @RequestBody AppMemoValueDeleteReq req){
		Integer result = appMemoService.delete(RequestContext.currentOrgId(), appId, RequestContext.currentUserId(), req);
		return Result.ok(result);
	}

}
