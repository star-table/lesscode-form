package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="更新备忘录数据请求结构体", description="更新备忘录数据请求结构体")
public class AppMemoValueUpdateReq {

	@NotNull(message = "备忘录id不能为空")
	@ApiModelProperty("备忘录id")
	private Long id;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("内容")
	private String content;

	@ApiModelProperty("配置")
	private String config;

	@ApiModelProperty("闹钟时间")
	private String alarmTime;

	@ApiModelProperty("是否完成 1：是 2：否")
	private Integer isCompletion;

	@ApiModelProperty("完成时间")
	private String completionTime;

	@ApiModelProperty("备忘录类型（0：普通备忘录 1：星标备忘录）")
	private Integer type;

}
