package com.polaris.lesscode.form.req;

import com.polaris.lesscode.form.internal.enums.MemoType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="添加备忘录数据请求结构体", description="添加备忘录数据请求结构体")
public class AppMemoValueAddReq {

	@NotNull(message = "备忘录标题不能为空")
	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("内容")
	private String content;

	@ApiModelProperty("配置")
	private String config;

	@ApiModelProperty("闹钟时间")
	private String alarmTime;

	@ApiModelProperty("备忘录类型（0：普通备忘录 1：星标备忘录）")
	private Integer type = MemoType.NONE.getType();

}
