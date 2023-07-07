package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
@ApiModel(value="备忘录返回信息", description="备忘录返回信息")
public class AppMemoResp{

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("创建人id")
    private Long creator;

    @ApiModelProperty("更新人id")
    private Long updator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("闹钟时间")
    private String alarmTime;

    @ApiModelProperty("备忘录类型")
    private Integer type;

    @ApiModelProperty("是否完成 1：是 2：否")
    private Integer isCompletion;

    @ApiModelProperty("完成时间")
    private String completionTime;

}
