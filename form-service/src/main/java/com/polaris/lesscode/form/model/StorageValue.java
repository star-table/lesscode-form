package com.polaris.lesscode.form.model;

import lombok.Data;

import java.util.Date;

@Data
public class StorageValue {

	private Long id;
	
	private String data;

	private Integer status;

	private Long creator;

	private Long updator;

	private Date updateTime;

	public StorageValue(Long id, String data, Integer status, Long creator, Long updator, Date updateTime) {
		this.id = id;
		this.data = data;
		this.status = status;
		this.creator = creator;
		this.updator = updator;
		this.updateTime = updateTime;
	}

	public StorageValue() {
	}
}
