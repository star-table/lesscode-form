package com.polaris.lesscode.form.exception;


import org.springframework.web.bind.annotation.ControllerAdvice;

import com.polaris.lesscode.exception.CommonsExceptionHandler;


//@Slf4j
@ControllerAdvice
public class ExceptionAdvice extends CommonsExceptionHandler{

	/*
	 * @ResponseBody
	 * 
	 * @ExceptionHandler(value = Exception.class) public Result<?>
	 * errorHandler(Exception ex) { log.error(ex.getMessage(), ex); return
	 * Result.error(ResultCode.SYS_ERROR); }
	 * 
	 * @ResponseBody
	 * 
	 * @ExceptionHandler(value = SysErrorException.class) public Result<?>
	 * errorHandler(SysErrorException ex) { log.error(ex.getMessage(), ex); return
	 * Result.error(ex.getCode(), ex.getMessage()); }
	 */
	
}