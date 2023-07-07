package com.polaris.lesscode.form.socket.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="备忘录返回信息", description="备忘录返回信息")
public class AppMemoSocketResp {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("闹钟时间")
    private String alarmTime;

}
