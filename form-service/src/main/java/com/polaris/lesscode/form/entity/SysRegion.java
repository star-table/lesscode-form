package com.polaris.lesscode.form.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: Liu.B.J
 * @date: 2021/1/20 17:04
 * @description:
 */
@Data
@TableName("lc_sys_region")
public class SysRegion {

    private Integer id;

    private String regionName;

    private Integer parentId;

    private String simpleName;

    private Integer level;

    private String cityCode;

    private String zipCode;

    private String merName;

    private Float lng;

    private Float lat;

    private String pinYin;

}
