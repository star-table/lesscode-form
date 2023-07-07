package com.polaris.lesscode.form.interceptor;

import org.springframework.stereotype.Component;

import com.polaris.lesscode.interceptor.RequestContextInterceptor;

import feign.RequestInterceptor;

@Component
public class FormRequestContextInterceptor extends RequestContextInterceptor implements RequestInterceptor{

}
