/**
 * 
 */
package com.polaris.lesscode.form.internal.feign;

import org.springframework.cloud.openfeign.FeignClient;

import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.fallback.AppformFallbackFactory;

/**
 * @author Bomb.
 *
 */
@FeignClient(value = ApplicationConsts.APPLICATION_FORM, fallbackFactory =  AppformFallbackFactory.class)
public interface AppFormProvider extends AppFormApi{

}
