/**
 * 
 */
package com.polaris.lesscode.form.service.tests;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polaris.lesscode.vo.Result;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Bomb.
 * just a test demo.
 */
@Validated
@RestController
@Api("validation test demo")
@RequestMapping("validate-test")
public class ValidateTestController {

	@ApiModelProperty("post request body test")
	@PostMapping("/post")
	public Result<?> testPostBody(@RequestBody @Valid MockBean mockBean){ 
		return Result.ok("参数校验通过");
	}
	
	
	@ApiModelProperty("get request parameter test")
	@GetMapping("/get")
	public Result<?> testGetParam(@RequestParam @NotBlank(message = "公司名称不能为空") @Size(min = 1,max = 6, message = "公司名字长度在{min}-{max}之间") String compName){
		return Result.ok("参数校验通过");
	}
	
	@ApiModel("mock bean")
	public class MockBean{
		
		@ApiModelProperty("姓名")
		@NotBlank(message = "姓名不能为空")
		private String name;
		
		@ApiModelProperty("年龄")
		@NotNull(message = "年龄不能为空")
		@Range(min = 1,max = 100,message = "年龄只能在{min}到{max}之间")
		private Integer age;
		
		@ApiModelProperty("邮箱")
		@Email(message = "邮箱格式不正确")
		private String email;
	}
}
