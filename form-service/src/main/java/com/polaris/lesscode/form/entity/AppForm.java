package com.polaris.lesscode.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_form")
public class AppForm {

    @TableId
    private Long id;

    private Long orgId;

    private Long appId;

    private Integer type;

    private String config;

    private Integer status;

    private Long creator;

    private Date createTime;

    private Long updator;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Version
    private Long version;

    @TableLogic(delval = "1", value = "2")
    private Integer delFlag;
}
