/**
 * 
 */
package com.polaris.lesscode.form.internal.feign;

import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.fallback.AppValueFallbackFactory;
import com.polaris.lesscode.form.internal.fallback.AppformFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = ApplicationConsts.APPLICATION_FORM, fallbackFactory =  AppValueFallbackFactory.class)
public interface AppValueProvider extends AppValueApi {

}
