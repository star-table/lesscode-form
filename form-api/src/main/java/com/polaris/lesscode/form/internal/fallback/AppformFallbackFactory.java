/**
 * 
 */
package com.polaris.lesscode.form.internal.fallback;


import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.form.internal.req.AppValueListReq;
import com.polaris.lesscode.form.internal.req.QuerySqlReq;
import com.polaris.lesscode.form.internal.resp.QuerySqlResp;
import com.polaris.lesscode.vo.Result;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.form.internal.api.AppFormApi;

import feign.hystrix.FallbackFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author bomb.
 *
 */
@Component
public class AppformFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppFormApi>{

	@Override
	public AppFormApi create(Throwable cause) {
		return new AppFormApi() {
			
			@Override
			public Result<QuerySqlResp> querySql(@Validated @RequestBody QuerySqlReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
					return Result.ok(new QuerySqlResp());
				});
			}
//
//			@Override
//			public Result<AppFormConfigResp> getFormConfig(Long appId, Long orgId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new AppFormConfigResp());
//				});
//			}
//
//			@Override
//			public Result<List<AppFormConfigResp>> getFormConfigs(GetFormConfigsReq req) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return null;
//				});
//			}
//
//			@Override
//			public Result<BizForm> getBizForm(Long orgId, Long appId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return null;
//				});
//			}
//
//			@Override
//			public Result<List<BizForm>> getBizForms(GetBizFormListReq req) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return null;
//				});
//			}
//
//			@Override
//			public Result<List<AppFormResp>> getFormByAppIds(List<Long> appIds) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new ArrayList<AppFormResp>());
//				});
//			}
//
//			@Override
//			public Result<List<FormFieldResp>> getFields(Long appId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new ArrayList<FormFieldResp>());
//				});
//			}
//
//			@Override
//			public Result<Integer> deleteForm(List<Long> appIds, Long userId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(0);
//				});
//			}
//
//			@Override
//			public Result<Boolean> saveBaseFields(AppFormBaseSaveReq internalReq) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(true);
//				});
//			}
//
//			@Override
//			public Result<AppFormResp> getFormByAppId(Long appId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new AppFormResp());
//				});
//			}
//
//			@Override
//			public Result<AppFormResp> getFormByAppId(Long orgId, Long appId) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new AppFormResp());
//				});
//			}
//
//			@Override
//			public Result<List<AppFormResp>> filter(AppFormFilter filter) {
//				return wrappDeal(ApplicationConsts.APPLICATION_FORM,cause,()->{
//					return Result.ok(new ArrayList<AppFormResp>());
//				});
//			}
		};
	}

}
