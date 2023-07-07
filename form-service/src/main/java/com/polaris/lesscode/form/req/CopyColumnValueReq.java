package com.polaris.lesscode.form.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 1:39 下午
 */
@Data
@ApiModel(value="复制列数据请求结构体", description="复制列数据请求结构体")
public class CopyColumnValueReq {

    private String sourceField;

    private String destField;

}
