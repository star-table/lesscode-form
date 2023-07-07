package com.polaris.lesscode.form.vo;

import com.polaris.lesscode.vo.AbstractResultCode;

public enum ResultCode implements AbstractResultCode {
	OK(0,"OK"),
	TOKEN_ERROR(401,"token错误"),
	FORBIDDEN_ACCESS(403,     "无权访问"),
	PATH_NOT_FOUND(404,   "请求地址不存在"),
	PARAM_ERROR(501,"请求参数错误"),
	INTERNAL_SERVER_ERROR(500, "服务器异常"),
	SYS_ERROR_MSG(996,  "系统异常, %s "),
	FAILURE(997,  "业务失败"),
	SYS_ERROR(998,    "系统异常"),
	UNKNOWN_ERROR(999,  "未知错误"),
	SOCKET_TIMEOUT_ERROR(1000,"网络超时请稍后再试"),
	UPDATA_SUCCESS(200,   "更新成功"),
	UPDATA_FAIL(100010,"更新失败"),
	INTERNAL_SERVICE_ERROR(150000,  "内部服务异常"),
	IMAGE_UPLOAD_FAIL(130001,  "图片上传失败"),

	APP_NOT_EXIST(450004,  "应用不存在"),
	APP_VALUE_ADD_FAIL(450006,       "应用数据添加失败"),
	APP_FORM_NOT_EXIST(450017,       "应用表单不存在"),
	APP_VALUE_ADD_CHECK_FAIL(4500023, "应用数据添加校验失败"),
	APP_FORM_ADD_FAIL(450024,       "应用表单添加失败"),
	APP_FORM_MODIFY_FAIL(450025,       "应用表单修改失败"),
	INIT_FORM_PERMISSION_GROUP_FAIL(450026,       "初始化表单权限组失败"),
	APP_VALUE_RECYCLE_FAIL(450030,       "回收数据失败"),
	APP_VALUE_RECOVER_FAIL(450031,       "恢复数据失败"),
	APP_VALUE_ENABLE_FAIL(450032,       "启用数据失败"),
	APP_VALUE_DISABLE_FAIL(450033,       "禁用数据失败"),
	SUMMARY_FORM_NOT_EXIST(450034,       "汇总表不存在"),

	//Form
	FORM_LIST_FILTER_ERROR(451001,  "表单列表过滤错误"),
	FORM_VALUE_REQUIRED(451002,        "表单字段值必填"),
	FORM_VALUE_LT_MINWORDCOUNT(451003,   "表单字段值少于最少字数"),
	FORM_VALUE_MT_MAXWORDCOUNT(451004,   "表单字段值多于最多字数"),
	FORM_VALUE_NOT_FORMATDATE(451005,  "表单字段值不符合日期格式"),
	FORM_VALUE_NOT_FORMATNUM(451006, "表单字段值不符合数字类型格式"),
	FORM_VALUE_NOT_DECIMAL(451007,  "表单字段值不允许是小数"),
	FORM_VALUE_MT_DECIMALDIGIT(451008,   "表单字段值超过限制位数"),
	FORM_VALUE_LT_MINVALUE(451009,  "表单字段值小于最小值"),
	FORM_VALUE_MT_MAXVALUE(451010,  "表单字段值大于最大值"),
	JSONNODE_FROM_ERROR(451011, "jsonNode转换异常"),
	FORM_VALUE_NOT_SELECTED(451012, "未选中表单数据"),
	FIELD_VALUE_NOT_BLANK(451013, "属性值不能为空"),
	FIELD_VALUE_VALIDATE_ERROR(451014, "表单数据校验失败: %s"),
	SUB_TABLE_NOT_EXIST(451015, "子表不存在"),
	FIELD_TYPE_NOT_EXIST(451016, "字段类型不存在"),
	ORDER_REFER_DATA_ID_IS_NULL(451017, "排序参照id为空"),
	ORDER_REFER_DATA_IS_NULL(451018, "排序数据不存在"),
	EXPORT_FIELDS_IS_EMPTY(451019, "导出字段列表为空"),
	IMPORT_TOKEN_NOT_MATCH_APP_ID(451020, "导入令牌appId不匹配"),
	IMPORT_TOKEN_INVALID(451021, "导入令牌失效或无效"),
	IMPORT_EXCEL_INVALID(451022, "导入的excel地址无效"),
	IMPORT_OUT_OF_LIMIT_ERROR(451023, "只允许导入50000行，200列以内的excel"),
	NO_DELETE_CONDITION_ERROR(451024, "无删除条件"),
	FORM_HEADER_REPEAT_ERROR(451025, "表头存在重复值"),
	BASE_FIELDS_BE_REFERENCED(451026, "有表单使用该团队字段"),


	FORM_OP_NO_BATCH_PRINT(452001,"无批量打印权限"),
	FORM_OP_NO_BATCH_UPDATE(452002,"无批量修改权限"),
	FORM_OP_NO_COPY(452003,"无复制权限"),
	FORM_OP_NO_CREATE(452004,"无添加权限"),
	FORM_OP_NO_DELETE(452005,"无删除权限"),
	FORM_OP_NO_EXPORT(452006,"无导出权限"),
	FORM_OP_NO_IMPORT(452007,"无导入权限"),
	FORM_OP_NO_READ(452008,"无查看权限"),
	FORM_OP_NO_UPDATE(452009,"无编辑权限"),
	NO_READ_FORM_PERMISSION(450027,       "该用户没有表单查询权限"),
	NO_ADD_FORM_PERMISSION(450028,       "该用户没有表单创建权限"),
	NO_MODIFY_FORM_PERMISSION(450029,       "该用户没有表单修改权限"),


	// Memo
	MEMO_VALUE_NOT_NULL(453002,  "备忘录内容不能为空"),
	MEMO_VALUE_ADD_FAIL(453003,       "备忘录内容添加失败"),
	MEMO_RELATION_ADD_FAIL(453004,       "备忘录关联添加失败"),
	MEMO_RELATION_DEL_FAIL(453005,       "备忘录关联删除失败"),
	MEMO_WORD_DOWNLOAD_FAIL(453006,  "备忘录word下载失败"),
	MEMO_PDF_DOWNLOAD_FAIL(453007,  "备忘录pdf下载失败"),
	DATE_FORMAT_FAIL(453008,  "日期格式错误"),
	MEMO_VALUE_UPDATE_FAIL(453009,  "备忘录更新失败"),
	OTHER_USER_IN_OPERATION(453010,  "由用户正在操作中，请稍后重试"),


	//Open
	INVALID_DATA_ID(460001, "无效的数据id"),

	;

	private ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private int code;

	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ResultCode parse(int code) {
		for (ResultCode rc : values()) {
			if (rc.getCode() == code) {
				return rc;
			}
		}
		return ResultCode.SYS_ERROR;
	}

	public boolean equals(Integer code) {
		return Integer.valueOf(this.getCode()).equals(code);
	}


}
