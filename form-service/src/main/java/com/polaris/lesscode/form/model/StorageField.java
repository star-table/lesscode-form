package com.polaris.lesscode.form.model;

import com.polaris.lesscode.enums.StorageFieldType;

import lombok.Data;

@Data
public class StorageField {

	/**
	 * 格式：_field_${timestamp}
	 */
	private String name;
	
	private StorageFieldType type;

	public StorageField(String name, StorageFieldType type) {
		this.name = name;
		this.type = type;
	}

	public StorageField() {
	}
	
}
