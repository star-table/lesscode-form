package com.polaris.lesscode.form.internal.enums;

public enum FormValueStatus {

	ENABLED(1, "启用"),

	DISABLE(2, "禁用"),

	GARBAGE(3, "回收站");

	private int status;

	private String name;

	private FormValueStatus(int status, String name) {
		this.status = status;
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
