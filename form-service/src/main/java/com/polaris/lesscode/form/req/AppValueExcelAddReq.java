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
@ApiModel(value="添加excel表单数据请求结构体", description="添加excel表单数据请求结构体")
public class AppValueExcelAddReq {

    private List<Map<Integer, Object>> form;

}
