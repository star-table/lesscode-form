package com.polaris.lesscode.form.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("lc_app_value")
public class AppValue {

	private Long id;
	
	private Long appId;
	
	private String content;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	@TableField(update = "now()")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	@Version
	private Long version;
	
	private Integer delFlag;
}
