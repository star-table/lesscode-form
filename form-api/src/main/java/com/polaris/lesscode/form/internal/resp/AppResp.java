package com.polaris.lesscode.form.internal.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppResp {
    private Long id;
    private Long pkgId;
    private Long orgId;
    private Long extendsId;
    private String name;
    private Integer type;
    private String icon;
    private Integer status;
    private Long creator;
    private Date createTime;
    private Long updator;
    private Date updateTime;
    private Long parentId;
    @ApiModelProperty("表单id")
    private Long formId;
    @ApiModelProperty("仪表盘id")
    private Long dashboardId;
}
