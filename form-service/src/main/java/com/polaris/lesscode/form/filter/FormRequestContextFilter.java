package com.polaris.lesscode.form.filter;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.filter.RequestContextFilter;

@Component
@WebFilter(filterName = "formRequestContextFilter", urlPatterns = "/*")
@Order(1)
public class FormRequestContextFilter extends RequestContextFilter implements Filter{

}
