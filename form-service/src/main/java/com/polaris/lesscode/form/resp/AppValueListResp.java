package com.polaris.lesscode.form.resp;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="表单数据集合返回结构体", description="表单数据集合返回结构体")
public class AppValueListResp {

    private List<? extends Map<String, Object>> datas;
    
    private Integer page;
    
    private Integer size;
    
    private Long total;
}
