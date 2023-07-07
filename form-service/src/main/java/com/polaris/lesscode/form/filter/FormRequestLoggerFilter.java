package com.polaris.lesscode.form.filter;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.filter.RequestLoggerFilter;

//@Component
//@WebFilter(filterName = "formRequestLoggerFilter", urlPatterns = "/*")
//@Order(2)
public class FormRequestLoggerFilter extends RequestLoggerFilter implements Filter{

}
