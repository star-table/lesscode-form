/**
 * 
 */
package com.polaris.lesscode.form.internal.fallback;

import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.req.*;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author bomb.
 *
 */
@Component
public class AppValueFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppValueApi>{

	@Override
	public AppValueApi create(Throwable cause) {
		return new AppValueApi() {

			@Override
			public Result<Page<Map<String, Object>>> filter(Long appId, AppValueListReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new Page<>());
				});
			}

			@Override
			public Result<List<Map<String, Object>>> filterRaw(AppValueListReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}

			@Override
			public Result<List<Map<String, Object>>> filterCustomStat(Long appId, AppValueListReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}

			@Override
			public Result<Long> filterStat(Long appId, AppValueListReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(0L);
				});
			}

			@Override
			public Result<?> add(Long appId, AppValueAddReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok();
				});
			}

			@Override
			public Result<List<Map<String, Object>>> update(Long appId, AppValueUpdateReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}

			@Override
			public Result<Boolean> updateBatchRaw(Long appId, AppValueUpdateBatchReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(true);
				});
			}

			@Override
			public Result<Boolean> del(Long appId, AppValueDeleteReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(true);
				});
			}

			@Override
			public Result<Integer> recycle(Long appId, AppRecycleReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(0);
				});
			}

			@Override
			public Result<Integer> recover(Long appId, AppRecycleReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(0);
				});
			}

//			@Override
//			public Result<?> addSubValues(Long appId, Long dataId, String fieldKey, SubValueAddReq req) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok();
//				});
//			}

//			@Override
//			public Result<?> updateSubValues(Long appId, Long dataId, String fieldKey, SubValueUpdateReq req) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok();
//				});
//			}

//			@Override
//			public Result<?> deleteSubValues(Long appId, Long dataId, String fieldKey, SubValueDeleteReq req) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok();
//				});
//			}

			@Override
			public Result<Map<Long, Double>> movingData(Long appId, AppValueMovingReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new HashMap<>());
				});
			}
		};
	}

}
