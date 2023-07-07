package com.polaris.lesscode.form.resp;

import com.polaris.lesscode.form.bo.FieldAuthBo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel(value="表单返回结构体", description="表单返回结构体")
public class AppFormResp {

	private Long id;

	private String name;
	
	private Long orgId;
	
	private Long appId;

	private Integer type;

	private String config;
	
	private Integer status;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;

	// 权限相关
	private boolean hasRead = false;

	private boolean hasCreate = false;

	private boolean hasUpdate = false;

	private boolean hasDelete = false;

	private boolean hasCopy = false;

	private boolean hasImport = false;

	private boolean hasExport = false;

	private boolean hasBatchUpdate = false;

	private boolean hasBatchPrint = false;

	private Map<String, Map<String, FieldAuthBo>> fieldAuths;

	public AppFormResp() {
		fieldAuths = new HashMap<>();
	}

}
